package crest.jira.data.retriever.model;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

import org.codehaus.jackson.annotate.JsonIgnoreProperties;

@JsonIgnoreProperties({ "avatarUrls" })
@DatabaseTable(tableName = "Project")
public class Project extends JiraEntity {

  @DatabaseField(foreign = true, columnName = "projectCategoryId")
  private ProjectCategory projectCategory;

  public ProjectCategory getProjectCategory() {
    return projectCategory;
  }

  public void setProjectCategory(ProjectCategory projectCategory) {
    this.projectCategory = projectCategory;
  }
}
