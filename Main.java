

import java.util.ArrayList;
import java.util.List;

class Main{

  public static void main(String[] args) {

    // No args supplied, print usage
    if (args.length < 1){
      System.out.println("Usage:\t java Main <formula in quotation marks> [<options>]\n");
      System.out.println("or\t java Main <Integer> [<options>]\n");
      //Add unicode guide
      //System.out.println("Logical symbols:\n");
      //System.out.println("\u2192 - implies");
      System.out.println("Examples of valid formulas:\n");
      System.out.println("\tA and (B and C) equivalent (A and B) and C");
      System.out.println("\tnot (A and B) equivalent not A or not B");
      System.out.println("\talways A implies A");
      System.out.println("\tnext A implies eventually A");
      System.out.println("\tnot A and not C and (A or B) and (C or D) and not D and (Y or R) and (not A or not B)");
      System.out.println("\tnot A and S and not Q and not C and (A or F) and (C or D) and (Y or R) and (not A or not B) and G and K and (not B or K) and U and T and not L and K and M and 8 and 9 and 1 and (not 8 or H or 9)");
      //System.out.println("\tall x exist y p(x) or q(y)");
      System.exit(1);
    }

    // No options supplied, print options
    if (args.length < 2){
      System.out.println("No options specified.\n");
      System.out.println("\t-b\tbrute-force");
      System.out.println("\t-bu\tbrute-force with unit clause considerations");
      System.out.println("\t-dpll\toriginal dpll-algorithm");
      System.out.println("\t-v\tgenerates a satisfiable CNF formula");
      System.out.println("\t-unsat\tgenerates an unsatisfiable CNF formula (default)");
      System.out.println("\t-first\tremoves the first clause in the generated unsatisfiable formula to make it satisfiable");
      System.out.println("\t-last\tremoves the last clause in the generated unsatisfiable formula to make it satisfiable (default)");
      System.out.println("\t-unit\tadds a unit clause to the generated unsatisfiable formula to make it satisfiable");
      System.exit(1);
    }

    // Extract options
    List<String> optList = new ArrayList<>();
    for (int i = 1; i < args.length; i++){
      switch (args[i].charAt(0)){
        case '-':
          if (args[i].length() < 2)
            throw new IllegalArgumentException("Not a valid argument: "+args[i]);
          else {
            if (args.length == 1)
              throw new IllegalArgumentException("Expected arg after: "+args[i]);
            optList.add(args[i]);
            continue;
          }
        default:
          System.out.println("Ignoring unaccepted args." + args[i]);
          break;
      }
    }

    // Create a PropositionalFormula, which is either auto-generated or based on supplied formula.
    try{
      PropositionalFormula myFormula = (isInteger(args[0])) ? new PropositionalFormula(generateFormula(Integer.parseInt(args[0]), optList.contains("-v"), optList.contains("-first"), optList.contains("-unit"))) : new PropositionalFormula(args[0]);

      System.out.println("\nFormula is a syntactically valid propositional formula.");
      if (myFormula.isCNF()){
        Solver solver = new Solver(myFormula);
        System.out.println("\nFormula is in CNF.");
        System.out.printf("\nThe formula written as a set of clauses is {%s}.\n\n", myFormula.getCNF());

        
        if (optList.contains("-b")){
          long time = System.nanoTime();
          int interpretation = solver.bruteForce();
          if (interpretation >= 0){
            System.out.println("Bruteforce method successful: " + interpretation);
          } else {
            System.out.println("\nBruteforce method failed.");
          }
          long step = System.nanoTime() - time;
          System.out.println("Time (ns) taken for bruteforce: " + step);
        }
        // Can be faster than regular brute-force, but same worst-case.
        if (optList.contains("-bu")){
          long time = System.nanoTime();
          int interpretation = solver.bruteForceUnit();
          if (interpretation >= 0){
            System.out.println("Bruteforce method with unit clauses successful: " + interpretation);
          } else {
            System.out.println("\nBruteforce method with unit clauses failed.");
          }
          long step = System.nanoTime() - time;
          System.out.println("Time (ns) taken for bruteforce with unit clauses: " + step);
        }
       
        if (optList.contains("-dpll")){
          long time = System.nanoTime();
          int interpretation = solver.dpll();
          if (interpretation >= 0){
            System.out.println("DPLL successful: " + interpretation);
          } else {
            System.out.println("\nDPLL failed.");
          }
          long step = (System.nanoTime() - time)/1000000;
          System.out.println("Time (us) taken for DPLL: " + step);
        }


      };
    } catch(Exception e){
      e.printStackTrace();
    }
  }

  /**
  * Generates a propositional CNF formula based on a given number of literals.
  * Generates maximum number of clauses based on the number of literals.
  * Note: this formula is a massive bottleneck, and becomes more or less useless
  * for n>15.
  *
  * @param n Number of literals to be in formula.
  * @param valid If true, generates a satisfiable formula by removing a clause.
  * @param first If true, removes the first clause to generate the satisfiable formula. If false, it removes the last clause.
  * @param unit If true, adds a unit clause to the formula in an attempt to make it satisfiable.
  * @return String a propositional CNF formula that can be supplied as a argument to PropositionalFormula.
  */
  private static String generateFormula(int n, boolean valid, boolean first, boolean unit){
    Character[] literals = {'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j', 'k', 'l', 'm', 'n', 'o', 'p', 'q', 'r', 's', 't', 'u', 'v', 'w', 'x', 'y', 'z', '2', '3', '4', '5', '6', '7', '8', '9'};
    int i = n;
    if (i > 31){
      i = 31;
      System.out.println("Highest current allowed formula length is 31.");
    }
    int max = (valid) ? (1 << n)-1 : 1 << n;
    int check = (first) ? 1 : 0;
    String s = "";

    for (int v = 0; v < max; v++){
      s = s.concat("(");
      for (int u = 0; u < i; u++){
        if (u != 0){
          s = s.concat(" or ");
        }
        if ((1 << u & check) > 0)
          s = s.concat("not ");
        s = s.concat(Character.toString(literals[u]));
      }
      s = s.concat(")");
      if (v < max - 1)
        s = s.concat(" and ");
      check++;
    }
    if (unit) {
      s = s.concat((first) ? " and not a" : " and a");
    }
    return s;
  }

  /**
  * Checks if a given string is an integer.
  *
  * @param input String to check if interger.
  * @return boolean True if param is integer, else false.
  */
  private static boolean isInteger( String input ) {
      try {
          Integer.parseInt( input );
          return true;
      }
      catch( Exception e ) {
          return false;
      }
  }

}
