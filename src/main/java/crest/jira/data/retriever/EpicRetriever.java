package crest.jira.data.retriever;

import crest.jira.data.retriever.map.IssueListMapper;
import crest.jira.data.retriever.map.IssueWithCustomFields;
import crest.jira.data.retriever.map.ResponseList;
import crest.jira.data.retriever.model.Epic;
import crest.jira.data.retriever.model.Field;

import java.text.ParseException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.Invocation.Builder;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.GenericType;

public class EpicRetriever extends BaseRetriever {

  private static final String EPIC_PATH = "/rest/agile/latest/epic/{epicId}";
  private static final String EPIC_PER_BOARD_PATH = "/{boardId}/epic";
  private Field[] fields;
  private PaginationBuilder<Epic> paginationBuilder;

  /**
   * Produces an instance for retrieving Epic information.
   * 
   * @param client
   *          Jersey Client.
   * @param configuration
   *          Repository configuration.
   * @param fields
   *          Field meta-data.
   */
  public EpicRetriever(Client client, JiraApiConfiguration configuration, Field[] fields) {
    super(client, configuration);
    this.fields = fields;
    this.paginationBuilder = new PaginationBuilder<Epic>(this);
  }

  /**
   * Returns Epic information.
   * 
   * @param epicId
   *          Epic identifier.
   * @return Epic instance.
   */
  public Epic getEpic(String epicId) {
    String uri = getConfiguration().getHostAndContext() + EPIC_PATH;
    WebTarget target = getClient().target(uri).resolveTemplate("epicId", epicId);

    return getBuilder(target).get(Epic.class);
  }

  /**
   * Returns all epics from a board.
   * 
   * @param mesosBoardId
   *          Board identifier.
   * @return List of Epics.
   * @throws ParseException
   *           In case of map parsing issues.
   */
  public List<Epic> getEpics(String mesosBoardId) throws ParseException {
    String uri = getConfiguration().getHostAndContext() + BoardRetriever.ALL_BOARDS_RESOURCE
        + EPIC_PER_BOARD_PATH;
    WebTarget target = getClient().target(uri).resolveTemplate("boardId", mesosBoardId);

    List<Epic> epics = paginationBuilder.get(target, new GenericType<ResponseList<Epic>>() {
    }, null);
    return epics;
  }

  /**
   * Returns a list of issues related to an epic.
   * 
   * @param mesosBoardId
   *          Board identifier.
   * @param epicId
   *          Epic identifier.
   * @return List of issues.
   * @throws ParseException
   *           In case of Date Parsing problems.
   */
  public ResponseList<IssueWithCustomFields> getIssuesForEpic(String mesosBoardId, String epicId)
      throws ParseException {
    String uri = getConfiguration().getHostAndContext() + BoardRetriever.ALL_BOARDS_RESOURCE
        + EPIC_PER_BOARD_PATH + "/{epicId}/issue";
    Map<String, Object> templateValues = new HashMap<String, Object>();
    templateValues.put("boardId", mesosBoardId);
    templateValues.put("epicId", epicId);

    WebTarget target = getClient().target(uri).resolveTemplates(templateValues);

    Builder builder = getBuilder(target);

    IssueListMapper issueListMapper = new IssueListMapper(fields);
    return issueListMapper.map(builder.get(Map.class));

  }
}
