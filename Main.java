

class Main{

  public static void main(String[] args) {
    if (args.length != 1){
      System.out.println("Usage:\t java Main <formula in quotation marks>\n");
      //Add unicode guide
      //System.out.println("Logical symbols:\n");
      //System.out.println("\u2192 - implies");
      System.out.println("Examples of valid formulas:\n");
      System.out.println("\tA and (B and C) equivalent (A and B) and C");
      System.out.println("\tnot (A and B) equivalent not A or not B");
      System.out.println("\talways A implies A");
      System.out.println("\tnext A implies eventually A");
      System.out.println("\tnot A and not C and (A or B) and (C or D) and not D and (Y or R) and (not A or not B)");
      //System.out.println("\tall x exist y p(x) or q(y)");
      System.exit(1);
    }

    /** Useful if I decide to change how to accept input
    if (args[0].charAt(0) != 34){
      System.out.println("Input error:\t Missing initial quotation marks.");
    }
    if (args[0].charAt(args[0].length() - 1) != 34){
      System.out.println("Input error:\t Missing trailing quotation marks.");
      System.exit(1);
    }
    **/

    try{
      PropositionalFormula myFormula = new PropositionalFormula(args[0]);
      System.out.printf("Formula '%s' is a syntactically valid propositional formula.\n", myFormula.getFormula());
      if (myFormula.isCNF()){
        System.out.printf("Formula '%s' is in CNF.\n", myFormula.getFormula());
        System.out.printf("The formula written in CNF is {%s}.\n", myFormula.getCNF());

        int interpretation = myFormula.bruteForce();
        if (interpretation >= 0){
          System.out.println("Bruteforce method successful: " + interpretation);
        } else {
          System.out.println("Bruteforce method failed.");
        }
      };
    } catch(Exception e){
      e.printStackTrace();
    }
  }
}
