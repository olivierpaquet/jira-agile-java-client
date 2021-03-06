package crest.jira.data.retriever.model;

import com.j256.ormlite.table.DatabaseTable;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;

/**
 * An issue can be resolved in many ways, only one of them being 'Fixed'. A
 * resolution is usually set when the status is changed.
 * 
 * <li>Fixed — A fix for this issue has been implemented.
 * <li>Won't Fix — This issue will not be fixed.
 * <li>Duplicate — This issue is a duplicate of an existing issue.
 * <li>Incomplete — There is not enough information to work on this issue.
 * <li>Cannot Reproduce — This issue could not be reproduced at this time, or
 * not enough information was available to reproduce the issue.
 * <li>Won't Do — This issue won't be actioned.
 * 
 * @see <a href=
 *      "https://confluence.atlassian.com/jira/what-is-an-issue-270829373.html">
 *      What is an Issue, from JIRA User's Guide </a>
 * 
 * @author Carlos G. Gavidia
 */
@DatabaseTable(tableName = "Resolution")
public class Resolution extends JiraEntity {

  public static final String NOT_A_PROBLEM = "8";
  public static final String INVALID = "6";
  public static final String WONT_FIX = "2";
  public static final String INCOMPLETE = "4";
  public static final String CANNOT_REPRODUCE = "5";
  public static final String UNRESOLVED = "9";

  @Override
  public String toString() {
    return new ToStringBuilder(this).append("name", this.getName()).append("id", this.getId())
        .toString();
  }

  @Override
  public int hashCode() {
    return new HashCodeBuilder().append(this.getId()).toHashCode();
  }

  @Override
  public boolean equals(Object obj) {
    if (obj == null) {
      return false;
    }
    if (obj == this) {
      return true;
    }
    if (obj.getClass() != getClass()) {
      return false;
    }

    JiraEntity rhs = (JiraEntity) obj;
    return new EqualsBuilder().append(this.getId(), rhs.getId()).isEquals();
  }
}
