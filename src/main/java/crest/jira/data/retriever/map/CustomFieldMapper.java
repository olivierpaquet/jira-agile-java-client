package crest.jira.data.retriever.map;

import crest.jira.data.retriever.model.CustomFieldsCatalog;
import crest.jira.data.retriever.model.Field;
import crest.jira.data.retriever.model.Sprint;
import crest.jira.data.retriever.model.User;
import crest.jira.data.retriever.model.Version;

import org.codehaus.jackson.map.ObjectMapper;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class CustomFieldMapper {

  private Field[] fields;
  private DateFormat dateFormat;
  private ObjectMapper objectMapper;

  /**
   * Initializes attributes.
   * 
   * @param fields
   *          Field catalog.
   */
  public CustomFieldMapper(Field[] fields) {
    this.fields = fields;
    this.objectMapper = new ObjectMapper();
    this.dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
  }

  /**
   * Extracts the custom field information from a JSON representation.
   * 
   * @param issueMap
   *          JSON representation.
   * @return Custom Field information.
   * @throws ParseException
   *           In case of Date parsing problems.
   */
  @SuppressWarnings("rawtypes")
  public CustomFieldsCatalog map(Map issueMap) throws ParseException {
    CustomFieldsCatalog catalog = new CustomFieldsCatalog();
    Map issueFieldsMap = (Map) issueMap.get("fields");

    Object fieldValue = getFieldValue("Story Points", issueFieldsMap);
    if (fieldValue != null) {
      catalog.setStoryPoints((Double) fieldValue);
    }

    catalog.setSprint(getSprints(issueFieldsMap));
    Object dateFirstResponse = getFieldValue("Date of First Response", issueFieldsMap);

    if (dateFirstResponse != null) {
      catalog.setDateOfFirstResponse(dateFormat.parse((String) dateFirstResponse));
    }

    catalog.setGlobalRank((String) getFieldValue("Global Rank", issueFieldsMap));
    catalog.setTimeInStatus((String) getFieldValue("Time in Status", issueFieldsMap));
    catalog.setAttachmentCount(
        Double.parseDouble((String) getFieldValue("Attachment count", issueFieldsMap)));
    catalog.setTargetVersion(objectMapper
        .convertValue(getFieldValue("Target Version/s", issueFieldsMap), Version[].class));
    catalog.setRank((String) getFieldValue("Rank", issueFieldsMap));
    catalog.setShepherd(
        objectMapper.convertValue(getFieldValue("Shepherd", issueFieldsMap), User.class));

    return catalog;
  }

  @SuppressWarnings({ "rawtypes", "unchecked" })
  private Sprint[] getSprints(Map issueFieldsMap) throws ParseException {
    Field field = this.getFieldByName("Sprint");
    ArrayList<String> sprintAsList = (ArrayList<String>) issueFieldsMap.get(field.getId());
    List<Sprint> sprints = new ArrayList<Sprint>();
    DateFormat customDateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");

    if (sprintAsList != null && sprintAsList.size() > 0) {

      for (String sprintAsString : sprintAsList) {
        Sprint sprint = new Sprint();

        int openBracket = sprintAsString.indexOf('[');
        int closeBracket = sprintAsString.indexOf(']');
        String[] tokens = sprintAsString.substring(openBracket + 1, closeBracket).split(",");

        for (String token : tokens) {
          int equalIndex = token.indexOf('=');
          String value = token.substring(equalIndex + 1);
          if (token.indexOf("id") == 0 && value.indexOf("null") < 0) {
            sprint.setId(value);
          }

          if (token.indexOf("rapidViewId") == 0 && value.indexOf("null") < 0) {
            sprint.setOriginBoardId(value);
          }

          if (token.indexOf("state") == 0 && value.indexOf("null") < 0) {
            sprint.setState(value);
          }

          if (token.indexOf("name") == 0 && value.indexOf("null") < 0) {
            sprint.setName(value);
          }

          if (token.indexOf("startDate") == 0 && value.indexOf("null") < 0) {
            sprint.setStartDate(customDateFormat.parse(value));
          }

          if (token.indexOf("endDate") == 0 && value.indexOf("null") < 0) {
            sprint.setEndDate(customDateFormat.parse(value));
          }

          if (token.indexOf("completeDate") == 0 && value.indexOf("null") < 0) {
            sprint.setCompleteDate(customDateFormat.parse(value));
          }
        }

        sprints.add(sprint);
      }
    }

    return sprints.toArray(new Sprint[sprints.size()]);
  }

  @SuppressWarnings("rawtypes")
  private Object getFieldValue(String fieldName, Map issueFieldsMap) {
    Field field = this.getFieldByName(fieldName);
    return issueFieldsMap.get(field.getId());
  }

  private Field getFieldByName(String fieldName) {
    for (Field field : fields) {
      if (fieldName.equals(field.getName())) {
        return field;
      }
    }

    return null;
  }
}
