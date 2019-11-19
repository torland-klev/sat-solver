


class Formula{

  private final String initialFormula;

  /**
   * Constructor assumes that formula is syntactically valid.
   *
   * @param syntacticallyValidFormula a syntactically valid fomula.
   **/
  public Formula(String syntacticallyValidFormula){
    trimParentheses(syntacticallyValidFormula);
    initialFormula = syntacticallyValidFormula;
  }

  //Remove equally many leading and trailing parantheses, if they exist.
  protected void trimParentheses(String s){
    while(true){
      if (s.charAt(0) == '(' && s.charAt(1) == '(' && s.charAt(s.length() - 1) == ')'){
        s = s.substring(1,s.length()-1);
      }
      else {
        break;
      }
    }
  }

  public String getFormula(){
    return initialFormula;
  }


}
