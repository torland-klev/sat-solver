
import java.util.Arrays;
import java.lang.IllegalArgumentException;


class SyntacticallyValidFormula extends Formula{

  private String myFormula;

  public SyntacticallyValidFormula(String syntacticallyValidFormula){
    super(syntacticallyValidFormula);
    trimParentheses(syntacticallyValidFormula);
    if(!(isSyntacticallyValid(syntacticallyValidFormula))){
      throw new IllegalArgumentException("Supplied argument is not a syntactically valid formula.");
    }
  }

  /**
   * Determines if the supplied argument is syntactically
   * valid.
   * <p>
   * This method always return immediately, whether or not
   * the formula is syntactically- or semantically valid.
   *
   * @param formula logic formula in text form
   * @return true if input is syntactically valid, else false.
   **/
  private boolean isSyntacticallyValid(String formula){

    String[] split = formula.split(" ");
    String[] binaryWords = {"and", "or", "implies", "equivalent", "until"};
    String[] unaryWords = {"always", "eventually", "until", "next", "not"};
    int leftParantheses = 0;
    int rightParantheses = 0;

    String prevFormula = null;

    boolean bad = false;
    boolean first = true;

    for (String s : split){

      if(s.length() > 0 && s.charAt(0) == '('){
        s = s.substring(1);
        leftParantheses++;
      }

      if (s.length() == 1){
        //System.out.println("Atom.");
        if (prevFormula == null){
          first = false;
          prevFormula = s;
          continue;
        }

        if (prevFormula.length() == 1){
          System.out.printf("Atom '%s' after atom 's'\n.", s, prevFormula);
          bad = true;
        }
        prevFormula = s;
        continue;
      }

      if (Arrays.stream(unaryWords).anyMatch(s::equals)){
        if (first){
          prevFormula = s;
          first = false;
          continue;
        }
        if (prevFormula.length() == 1){
          System.out.printf("Unary operator '%s' after atom '%s'.\n", s, prevFormula);
          bad = true;
        }
        prevFormula = s;
        first = false;
        continue;
      }

      //
      if (first){
        System.out.printf("First term '%s' is not an atom, left parantheses + atom or unary operator.\n", s);
        prevFormula = s;
        bad = true;
        continue;
      }

      // Check if atom with trailing right parantheses.
      if (s.length() == 2 && s.charAt(1) == ')'){
        if (prevFormula.length() == 1){
          System.out.printf("Atom '%s' after previous atom '%s'.\n", s.charAt(0), prevFormula);
          bad = true;
        }
        rightParantheses++;
        prevFormula = s.substring(0,1);
        continue;
      }

      // Check if word is binary.
      if (Arrays.stream(binaryWords).anyMatch(s::equals)){
        if (prevFormula.length() != 1){
          System.out.printf("Binary operator '%s' after operator '%s'.\n", s, prevFormula);
          bad = true;
        }
        prevFormula = s;
        continue;
      }

      System.out.println("Bad word: " + s);
      bad = true;
    }

    if (prevFormula.length() != 1){
      System.out.println("Formula does not end in an atom.\n");
      bad = true;
    }
    if (leftParantheses > rightParantheses){
      System.out.println("More left parantheses than right parantheses.");
      bad = true;
    }
    if (leftParantheses < rightParantheses){
      System.out.println("More right parantheses than left parantheses.");
      bad = true;
    }
    if (bad) {
      System.out.println("Formula is syntactically invalid.");
      return false;
    }

    return true;
  }

}
