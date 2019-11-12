

import java.util.ArrayList;
import java.util.List;

class Main{

  public static void main(String[] args) {
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

    if (args.length < 2){
      System.out.println("No options specified.\n");
      System.out.println("\t-b\tbrute-force");
      System.out.println("\t-bu\tbrute-force with unit clause considerations");
      System.out.println("\t-v\tgenerates a satisfiable CNF formula");
      System.out.println("\t-unsat\tgenerates an unsatisfiable CNF formula (default)");
      System.out.println("\t-first\tremoves the first clause in the generated unsatisfiable formula to make it satisfiable");
      System.out.println("\t-last\tremoves the last clause in the generated unsatisfiable formula to make it satisfiable (default)");
      System.out.println("\t-unit\tadds a unit clause to the generated unsatisfiable formula to make it satisfiable (default)");
      System.exit(1);
    }
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

    try{
      PropositionalFormula myFormula = (isInteger(args[0])) ? new PropositionalFormula(generateFormula(Integer.parseInt(args[0]), optList.contains("-v"), optList.contains("-first"), optList.contains("-unit"))) : new PropositionalFormula(args[0]);

      System.out.printf("\nFormula '%s' is a syntactically valid propositional formula.\n", myFormula.getFormula());
      if (myFormula.isCNF()){
        System.out.printf("\nFormula '%s' is in CNF.\n", myFormula.getFormula());
        System.out.printf("\nThe formula written as a set of clauses is {%s}.\n\n", myFormula.getCNF());

        //Bruteforce uses ~minute for 13 variables.
        if (optList.contains("-b")){
          long time = System.nanoTime();
          int interpretation = myFormula.bruteForce();
          if (interpretation >= 0){
            System.out.println("Bruteforce method successful: " + interpretation);
          } else {
            System.out.println("\nBruteforce method failed.");
          }
          long step = System.nanoTime() - time;
          System.out.println("Time (ns) taken for bruteforce: " + step);
        }

        if (optList.contains("-bu")){
          long time = System.nanoTime();
          int interpretation = myFormula.bruteForceUnit();
          if (interpretation >= 0){
            System.out.println("Bruteforce method with unit clauses successful: " + interpretation);
          } else {
            System.out.println("\nBruteforce method with unit clauses failed.");
          }
          long step = System.nanoTime() - time;
          System.out.println("Time (ns) taken for bruteforce with unit clauses: " + step);
        }


      };
    } catch(Exception e){
      e.printStackTrace();
    }
  }
  private static String generateFormula(int n, boolean valid, boolean first, boolean unit){
    Character[] literals = {'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j', 'k', 'l', 'm', 'n', 'o', 'p', 'q', 'r', 's', 't', 'u', 'v', 'w', 'x', 'y', 'z', '1', '2', '3', '4', '5', '6', '7', '8', '9'};
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
