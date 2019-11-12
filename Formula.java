


class Formula{

  private final String initialFormula;

  /**
   * Constructor assumes that formula is syntactically valid.
   *
   * @param syntacticallyValidFormula a syntactically valid fomula.
   **/
  public Formula(String syntacticallyValidFormula){
    initialFormula = trimParentheses(syntacticallyValidFormula);
  }

  protected String trimParentheses(String untrimmed){
    String trimmed = untrimmed;
    //Remove equally many leading and trailing parantheses, if they exist.
    while(true){
      if (trimmed.charAt(0) == '(' && trimmed.charAt(1) == '(' && trimmed.charAt(trimmed.length() - 1) == ')'){
        trimmed = trimmed.substring(1,trimmed.length()-1);
      }
      else {
        break;
      }
    }
    return trimmed;
  }

  public String getFormula(){
    return initialFormula;
  }


}
