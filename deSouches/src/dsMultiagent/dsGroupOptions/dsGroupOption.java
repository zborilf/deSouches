package dsMultiagent.dsGroupOptions;


public class dsGroupOption {

  public static final int __taskOption = 1;
  public static final int __dispenserSpottedOption = 2;
  public static final int __goalZoneSpottedOption = 3;
  public static final int __roleZoneSpottedOption = 4;
  public static final int __scenarioCompletedOption = 5;
  public static final int __scenarionFailedOption = 6;

  int POptionType;
  String POptionName;

  public String getOptionName() {
    return (POptionName);
  }
}
