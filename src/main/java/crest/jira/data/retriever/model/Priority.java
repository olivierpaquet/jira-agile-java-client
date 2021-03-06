package crest.jira.data.retriever.model;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

import org.apache.commons.lang.builder.ToStringBuilder;

/**
 * An issue's priority indicates its relative importance.
 * 
 * <li>Blocker — Highest priority. Indicates that this issue takes precedence
 * over all others.
 * <li>Critical — Indicates that this issue is causing a problem and requires
 * urgent attention.
 * <li>Major — Indicates that this issue has a significant impact.
 * <li>Minor — Indicates that this issue has a relatively minor impact.
 * <li>Trivial — Lowest priority.
 * 
 * @see <a href=
 *      "https://confluence.atlassian.com/jira063/what-is-an-issue-683542485.html">
 *      What is an Issue, from JIRA User's Guide </a>
 * 
 * @author Carlos G. Gavidia
 */
@DatabaseTable(tableName = "Priority")
public class Priority extends JiraEntity {

  @DatabaseField
  private String iconUrl;

  public Priority() {

  }

  public Priority(String id, String name) {
    this.setId(id);
    this.setName(name);
  }

  public String getIconUrl() {
    return iconUrl;
  }

  public void setIconUrl(String iconUrl) {
    this.iconUrl = iconUrl;
  }

  @Override
  public String toString() {
    return new ToStringBuilder(this).append("name", this.getName()).append("id", this.getId())
        .toString();
  }

}
