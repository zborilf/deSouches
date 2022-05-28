package dsAgents.dsBeliefBase.dsBeliefs;


import java.util.HashMap;

public class DSRoles {
  HashMap<String, DSRole> PRoles;

  public void addRole(DSRole role) {
    PRoles.put(role.getRoleName(), role);
  }

  public DSRole getRole(String roleName) {
    return (PRoles.get(roleName));
  }

  public DSRoles() {
    PRoles = new HashMap<String, DSRole>();
  }
}
