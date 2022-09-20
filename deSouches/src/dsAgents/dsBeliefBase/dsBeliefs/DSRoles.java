package dsAgents.dsBeliefBase.dsBeliefs;

import java.util.HashMap;

public class DSRoles {

  public static String _roleDefault="default";
  public static String _roleWorker="worker";
  public static String _roleDigger="digger";

  static int PWorkerAbility=0;
  static int PDiggerAbility=0;

  HashMap<String, DSRole> PRoles;

  public int getWorkerAbility(DSRole role){
    return(role.getSpeed(0)+role.getSpeed(1));
  }

  public int getDiggerAbility(DSRole role){
    return(role.PClearChance* role.PClearDistance);
  }

  public void addRole(DSRole role) {
    if(role.PActions.contains("submit")&&
            role.PActions.contains("attach")&&
            role.PActions.contains("connect")
    ) {
      if(getWorkerAbility(role)>PWorkerAbility){
        PWorkerAbility=getWorkerAbility(role);
        System.out.println("Role " + role.getRoleName() + " can work, ab:"+PWorkerAbility);
        _roleWorker=role.getRoleName();
      }
    }
    if(getDiggerAbility(role)>PDiggerAbility){
      PDiggerAbility=getDiggerAbility(role);
      System.out.println("Role " + role.getRoleName() + " can fight, ab:"+PDiggerAbility);
      _roleDigger=role.getRoleName();
  }


    PRoles.put(role.getRoleName(), role);
  }

  public DSRole getRole(String roleName) {
    return (PRoles.get(roleName));
  }

  public DSRoles() {
    PRoles = new HashMap<String, DSRole>();
  }
}
