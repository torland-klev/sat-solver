import java.util.Arrays;
import java.lang.IllegalArgumentException;

class PropositionalFormula extends SyntacticallyValidFormula {

  public PropositionalFormula(String syntacticallyValidFormula){
    super(syntacticallyValidFormula);
    if(!(isPropositional(trimParentheses(syntacticallyValidFormula)))){
      throw new IllegalArgumentException("Supplied argument is not a syntactically valid propositional formula.");
    }
  }

  private boolean isPropositional(String syntacticallyValidFormula){
    String[] approvedWords = {"and", "or", "implies", "equivalent", "not"};
    String[] split = syntacticallyValidFormula.split(" ");
    boolean bad = false;

    for (String s : split){
      if (s.charAt(0) == '('){
        s = s.substring(1);
      }
      if (!(Arrays.stream(approvedWords).anyMatch(s::equals)) && s.length() > 2){
        System.out.println("Disallowed word: " + s);
        bad = true;
      }
    }

    return !bad;
  }
}
