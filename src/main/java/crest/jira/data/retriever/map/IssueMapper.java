package crest.jira.data.retriever.map;

import crest.jira.data.retriever.model.Comment;
import crest.jira.data.retriever.model.Component;
import crest.jira.data.retriever.model.Epic;
import crest.jira.data.retriever.model.History;
import crest.jira.data.retriever.model.Issue;
import crest.jira.data.retriever.model.IssueType;
import crest.jira.data.retriever.model.Priority;
import crest.jira.data.retriever.model.Progress;
import crest.jira.data.retriever.model.Project;
import crest.jira.data.retriever.model.Resolution;
import crest.jira.data.retriever.model.Sprint;
import crest.jira.data.retriever.model.Status;
import crest.jira.data.retriever.model.User;
import crest.jira.data.retriever.model.Version;
import crest.jira.data.retriever.model.Votes;
import crest.jira.data.retriever.model.Watches;

import org.codehaus.jackson.map.ObjectMapper;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class IssueMapper {

  private ObjectMapper objectMapper;

  // TODO(cgavidia): Seems to be an issue with the hours. It seems to be an
  // hour off.
  private DateFormat dateFormat;

  /**
   * Initializes attributes.
   */
  public IssueMapper() {
    objectMapper = new ObjectMapper();
    dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ");
  }

  /**
   * Generates an Issue based on a Map representation of a JSON file.
   * 
   * @param issueMap
   *          JSON representation.
   * @return Issue instance.
   * @throws ParseException
   *           In case of date parsing problems.
   */
  @SuppressWarnings({ "rawtypes", "unchecked" })
  public Issue map(Map issueMap) throws ParseException {
    Issue issue = new Issue();

    Object expandMap = issueMap.get("expand");
    if (expandMap != null) {
      issue.setExpand((String) expandMap);
    }

    issue.setId((String) issueMap.get("id"));
    issue.setSelf((String) issueMap.get("self"));
    issue.setKey((String) issueMap.get("key"));

    Map issueFieldsMap = (Map) issueMap.get("fields");

    issue.setIssueType(objectMapper.convertValue(issueFieldsMap.get("issuetype"), IssueType.class));
    issue.setTimespent((Integer) issueFieldsMap.get("timespent"));
    issue.setProject(objectMapper.convertValue(issueFieldsMap.get("project"), Project.class));
    issue.setFixVersions(
        objectMapper.convertValue(issueFieldsMap.get("fixVersions"), Version[].class));
    issue.setAggregatetimespent((Integer) issueFieldsMap.get("aggregatetimespent"));
    issue.setResolution(
        objectMapper.convertValue(issueFieldsMap.get("resolution"), Resolution.class));

    String resolutionDate = (String) issueFieldsMap.get("resolutiondate");
    if (resolutionDate != null) {
      issue.setResolutiondate(dateFormat.parse(resolutionDate));
    }

    Integer workratio = (Integer) issueFieldsMap.get("workratio");
    if (workratio != null) {
      issue.setWorkratio(workratio);
    }

    Object lastViewedString = issueFieldsMap.get("lastViewed");
    if (lastViewedString != null) {
      issue.setLastViewed(dateFormat.parse((String) lastViewedString));
    }
    issue.setWatches(objectMapper.convertValue(issueFieldsMap.get("watches"), Watches.class));

    String createdDate = (String) issueFieldsMap.get("created");
    if (createdDate != null) {
      issue.setCreated(dateFormat.parse(createdDate));
    }

    issue.setEpic(objectMapper.convertValue(issueFieldsMap.get("epic"), Epic.class));
    issue.setPriority(objectMapper.convertValue(issueFieldsMap.get("priority"), Priority.class));
    ArrayList<String> labels = (ArrayList<String>) issueFieldsMap.get("labels");

    if (labels != null) {
      issue.setLabels(labels.toArray(new String[labels.size()]));
    }

    issue.setTimeestimate((Integer) issueFieldsMap.get("timeestimate"));
    issue.setAggregatetimeoriginalestimate(
        (Integer) issueFieldsMap.get("aggregatetimeoriginalestimate"));

    issue.setVersions(objectMapper.convertValue(issueFieldsMap.get("versions"), Version[].class));

    ArrayList<Map<String, Object>> issueLinksMap = (ArrayList<Map<String, Object>>) issueFieldsMap
        .get("issuelinks");

    if (issueLinksMap != null && issueLinksMap.size() > 0) {
      IssueLinksMapper issueLinkMapper = new IssueLinksMapper();
      issue.setIssuelinks(issueLinkMapper.map(issueLinksMap));
    }

    issue.setAssignee(objectMapper.convertValue(issueFieldsMap.get("assignee"), User.class));
    Object updatedString = issueFieldsMap.get("updated");

    if (updatedString != null) {
      issue.setUpdated(dateFormat.parse((String) updatedString));
    }

    issue.setStatus(objectMapper.convertValue(issueFieldsMap.get("status"), Status.class));
    issue.setComponents(
        objectMapper.convertValue(issueFieldsMap.get("components"), Component[].class));
    issue.setTimeoriginalestimate((Integer) issueFieldsMap.get("timeoriginalestimate"));
    issue.setDescription((String) issueFieldsMap.get("description"));
    issue.setTimetracking((Map) issueFieldsMap.get("timetracking"));
    issue.setAggregatetimeestimate((Integer) issueFieldsMap.get("aggregatetimeestimate"));

    Object flaggedObject = issueFieldsMap.get("flagged");
    if (flaggedObject != null) {
      issue.setFlagged((Boolean) flaggedObject);
    }

    issue.setSummary((String) issueFieldsMap.get("summary"));
    issue.setCreator(objectMapper.convertValue(issueFieldsMap.get("creator"), User.class));

    ArrayList<Map<String, Object>> subtasksMap = (ArrayList<Map<String, Object>>) issueFieldsMap
        .get("subtasks");

    // TODO(cgavidia): This should be a method. And every complex mapping.
    List<Issue> responseAsList = new ArrayList<Issue>();
    if (subtasksMap != null && subtasksMap.size() > 0) {
      IssueMapper issueMapper = new IssueMapper();

      for (Map<String, Object> subtaskMap : subtasksMap) {
        responseAsList.add(issueMapper.map((Map) subtaskMap));
      }

      issue.setSubtasks(responseAsList.toArray(new Issue[responseAsList.size()]));
    }

    issue.setReporter(objectMapper.convertValue(issueFieldsMap.get("reporter"), User.class));
    issue.setAggregateprogress(
        objectMapper.convertValue(issueFieldsMap.get("aggregateprogress"), Progress.class));
    issue.setEnvironment((String) issueFieldsMap.get("environment"));
    issue.setDuedate((String) issueFieldsMap.get("duedate"));
    issue.setProgress(objectMapper.convertValue(issueFieldsMap.get("progress"), Progress.class));

    Object commentInMap = issueFieldsMap.get("comment");
    if (commentInMap != null) {
      ResponseListMapper<Comment> commentListMapper = new ResponseListMapper<Comment>("comments",
          Comment.class);
      ResponseList<Comment> responseList = commentListMapper.map((Map) commentInMap);
      issue.setComment(responseList);
    }

    issue.setVotes(objectMapper.convertValue(issueFieldsMap.get("votes"), Votes.class));

    Object worklogInMap = issueFieldsMap.get("worklog");
    if (worklogInMap != null) {
      ResponseListMapper<Object> worklogListMapper = new ResponseListMapper<Object>("worklogs",
          Object.class);
      ResponseList<Object> responseList = worklogListMapper.map((Map) worklogInMap);
      issue.setWorklog(responseList);
    }

    issue.setClosedSprints(
        objectMapper.convertValue(issueFieldsMap.get("closedSprints"), Sprint[].class));
    issue.setSprint(objectMapper.convertValue(issueFieldsMap.get("sprint"), Sprint.class));

    Object changeLogInMap = issueMap.get("changelog");
    if (changeLogInMap != null) {
      ResponseListMapper<History> changeLogListMapper = new ResponseListMapper<History>("histories",
          History.class);
      ResponseList<History> responseList = changeLogListMapper.map((Map) changeLogInMap);
      issue.setChangeLog(responseList);
    }

    return issue;
  }

}
