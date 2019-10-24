import java.util.Arrays;
import java.lang.IllegalArgumentException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;

class PropositionalFormula extends SyntacticallyValidFormula {

  public String formula;
  public String CNF;

  public PropositionalFormula(String syntacticallyValidFormula){
    super(syntacticallyValidFormula);
    if(!(isPropositional(trimParentheses(syntacticallyValidFormula)))){
      throw new IllegalArgumentException("Supplied argument is not a syntactically valid propositional formula.");
    } else {
      this.formula = syntacticallyValidFormula;
    }
  }

  private boolean isPropositional(String syntacticallyValidFormula){
    String[] approvedWords = {"and", "or", "implies", "equivalent", "not"};
    return containsWord(syntacticallyValidFormula, approvedWords, true);
  }

  private void toCNF(){
    /* Algorithm:
       To convert first-order logic to CNF:

       1 Convert to Negation normal form.
         1 Eliminate implications: convert x → y to ¬ x ∨ y
         2 Move NOTs inwards.
       2 Standardize variables
       3 Skolemize the statement
       4 Drop universal quantifiers
       5 Distribute ANDs over ORs.
    */
  }

  public boolean isCNF(){
    return isCNF(this.formula);
  }

  /*
   * TODO: Check start of expression
   * TODO: What if there are no parantheses?
   */
  public boolean isCNF(String formula){
    boolean isCNF = true;
    if (containsWord(formula, new String[]{"implies"}, false)){
      System.out.printf("Formula '%s' is not in CNF as it contains an implication.\n", formula);
      isCNF = false;
    }
    if (containsWord(formula, new String[]{"equivalent"}, false)) {
      System.out.printf("Formula '%s' is not in CNF as it contains an equivalence.\n", formula);
      isCNF = false;
    }

    List<String> clauses = new ArrayList<>();
    String regexClauses = "\\(([^)]+)\\)";
    String regexConjunctions = "\\)([^)]+)\\(";
    Matcher m = Pattern.compile(regexClauses).matcher(formula);
    while(m.find()){
      //System.out.println(m.group(1));
      clauses.add(m.group(1));
    }
    m = Pattern.compile(regexConjunctions).matcher(formula);
    while(m.find()){
      if(!(m.group(1).equals(" and "))){
        System.out.printf("Formula '%s' is not in CNF as it contains operator '%s' between clauses.\n", formula, m.group(1));
        isCNF = false;
      }
    }


    String[] disallowedWords = {"and", "implies", "equivalent"};
    if(containsWord(clauses, disallowedWords, false)){
      System.out.printf("Formula '%s' is not in CNF as it contains a clause which is not a disjunction.\n", formula);
      isCNF = false;
    }


    /* Forgot that I did this check previously
    for (int i = 0; i < split.length(); i++){
      if (split[i].charAt[0] == '('){
        if (split[i].charAt[1] == ')'){
          System.out.println("Formula contains empty clause.");
          isCNF = false;
        }
        if (split[i].substring[1] == "not"){
          continue;
        }
        if (split[i].substring[1].length() == 1){
          continue;
        }
        System.out.printf("Word '%s' contains a non-(negated)atom after initial parantheses.\n", split[i]);
        isCNF = false;
      }
    }
    */
    return isCNF;
  }

  private boolean containsWord(List<String> formulas, String[] words, boolean shouldContain){
    boolean found = false;
    ListIterator<String> iterator = formulas.listIterator();
    while(iterator.hasNext()){
      if (containsWord(iterator.next(), words, shouldContain)){
        found = true;
      }
    }
    return found;
  }

  private boolean containsWord(String formula, String[] words, boolean shouldContain){
    String[] split = formula.split(" ");
    boolean contains = false;

    for (String s : split){
      if (s.charAt(0) == '('){
        s = s.substring(1);
      }
      if (Arrays.stream(words).anyMatch(s::equals)){

        if (!shouldContain){
          //System.out.println("Disallowed word: " + s);
        }

        contains = true;
      }
    }

    return contains;
  }

}
