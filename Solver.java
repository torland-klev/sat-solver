
import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;
import java.util.HashMap;

class Solver{

  private PropositionalFormula formula;
  private Character[] propositions;
  private int[] vector;
  private List<String> clauseSet;

  public Solver(PropositionalFormula formula){
    this.formula = formula;
    if (formula.isCNF()){
      this.propositions = extractPropositions(formula);
      this.vector = extractBitVector(propositions);
      this.clauseSet = extractClauseSet(formula);
    }
  }

  private Character[] extractPropositions(PropositionalFormula formula){

    String CNF = formula.getCNF();

    List<Character> props = new ArrayList<>();
    for (int i = 0; i < CNF.length(); i++){
      Character c = CNF.charAt(i);
      if ((c == '{') || (c == '}') || (c == ' ') || (c == ',') || (c == '-')){
        continue;
      }
      if (props.contains(c)){
        continue;
      }
      props.add(c);
    }
    Character[] propositions = new Character[props.size()];
    propositions = props.toArray(propositions);
    return propositions;
  }

  private int[] extractBitVector(Character[] propositions){
    //Initialize a new array able to hold as many bits as propositions.
    //An integer can hold 31 bits.
    int[] vector = new int[propositions.length / 32 + 1];
    int index = 0;
    int counter = 0;
    for (Character c : propositions){
      vector[index] = (vector[index] << 1) | 1;
      counter++;
      if (counter >=32 ){
        index++;
        counter = 0;
      }
    }
    return vector;
  }

  private List<String> extractClauseSet(PropositionalFormula formula){
    return formula.getClauseSet();
  }


  public int bruteForce(){
    return bruteForce(this.clauseSet, this.propositions, this.vector[0]);
  }
  private int bruteForce(List<String> clauses, Character[] propositions, int bitvector){

    // Map the literals to the interpreted value as decided by the bitvector.
    HashMap<Character, Boolean> interpretation = new HashMap<>();

    while(bitvector >= 0){
      int check = 1;
      interpretation.clear();
      for (Character c : propositions){
        interpretation.put(c, (bitvector & check) > 0);
        check = check << 1;
      }

      // Iterate through all clauses. If a clause is unsatisfied in the interpretation,
      // recurse with decremented bitvector.
      boolean sat = true;
      for (String s : clauses){
        if (!isClauseSatisfiable(s, interpretation)){
          sat = false;
          break;
        }
      }
      // All clauses satisfied by the interpretation.
      if (sat) {
        System.out.println("\nA satisfying interpretation: " + interpretation);
        return bitvector;
      }
      bitvector--;

    }
    return -1;
  }


  public int bruteForceUnit(){

    HashMap<Character, Boolean> unitClauses = new HashMap<>();
    for (String s : this.clauseSet){
      if (s.length() > 2)
        continue;
      if (s.charAt(0) == '-'){
        unitClauses.put(s.charAt(1), false);
      }
      if (s.length() == 1){
        unitClauses.put(s.charAt(0), true);
      }
    }
    return bruteForceUnit(this.clauseSet, this.propositions, this.vector[0], unitClauses);
  }

  private int bruteForceUnit(List<String> clauses, Character[] propositions, int bitvector, HashMap<Character, Boolean> unitClauses){

    // Map the literals to the interpreted value as decided by the bitvector.
    HashMap<Character, Boolean> interpretation = new HashMap<>();
    while(bitvector > 0){
      int check = 1;
      interpretation.clear();
      for (Character c : propositions){
        if (unitClauses.keySet().contains(c)){
          interpretation.put(c, unitClauses.get(c));
          if (!interpretation.get(c))
            bitvector &= (bitvector ^ check);
        } else {
          interpretation.put(c, (bitvector & check) > 0);
        }
        check = check << 1;
      }

      // Iterate through all clauses. If a clause is unsatisfied in the interpretation,
      // recurse with decremented bitvector.
      boolean sat = true;
      for (String s : clauses){
        if (!isClauseSatisfiable(s, interpretation)){
          sat = false;
          break;
        }
      }
      // All clauses satisfied by the interpretation.
      if (sat) {
        System.out.println("\nA satisfying interpretation: " + interpretation);
        return bitvector;
      }
      bitvector--;

    }
    return -1;
  }

  public int dpll(){
    return dpll(this.formula);
  }

  private int dpll(PropositionalFormula f){
    // Avoid making changes to input
    PropositionalFormula formula = f;

    if (isConsistentSetOfLiterals(formula)){
      System.out.printf("Formula %s is a consistent set of literals.\n", formula.getFormula());
      return 1;
    }
    if (containsEmptyClause(formula)){
      System.out.printf("Formula %s contains the empty clause.\n", formula.getFormula());
      return -1;
    }

    List<String> unitClauses = extractUnitClauses(formula);
    System.out.println(unitClauses);
    for (String literal : unitClauses){
      formula = unitPropagate(literal, formula);
    }

    return -1;
  }

  private boolean isConsistentSetOfLiterals(PropositionalFormula formula){
    List<String> clauseSet = formula.getClauseSet();
    List<Character> literals = new ArrayList<>();
    List<Character> negatedLiterals = new ArrayList<>();
    for (String clause : clauseSet){
      char[] clauseLiterals = clause.toCharArray();
      for (int i = 0; i < clauseLiterals.length; i++){
        char literal = clauseLiterals[i];
        if (i > 0 && clauseLiterals[i-1] == '-'){
          if (literals.contains(literal)){
            return false;
          }
          negatedLiterals.add(literal);
          continue;
        }
        if (negatedLiterals.contains(literal)){
          return false;
        }
        literals.add(literal);
      }
    }
    return true;
  }

  private boolean containsEmptyClause(PropositionalFormula formula){
    return (formula.getClauseSet().contains(null));
  }

  private List<String> extractUnitClauses(PropositionalFormula formula){
    List<String> list = new ArrayList<>();
    List<String> clauses = formula.getClauseSet();
    for (String clause : clauses){
      char[] clauseLiterals = clause.toCharArray();
      if (clauseLiterals.length == 1){
        list.add(clause);
        continue;
      }
      if (clauseLiterals.length == 2 && clauseLiterals[0] == '-'){
        list.add(clause);
        continue;
      }
    }
    return list;
  }

  private PropositionalFormula unitPropagate(String literal, PropositionalFormula f){
    PropositionalFormula formula = f;

    return formula;
  }

  private boolean isClauseSatisfiable(String clause, HashMap<Character, Boolean> interpretation){
    boolean negated = false;
    //Iterate through each literal in the clause.
    for (int i = 0; i < clause.length(); i++){
      char c = clause.charAt(i);
      if (c == '-'){
        negated = true;
        continue;
      }
      //True if literal is interpreted to true and not negated, or
      //if literal is interpreted to false and negated. Classical XOR.
      //If a literal is true, the clause is satisfied.
      if (interpretation.get(c) ^ negated){
        return true;
      }
      negated = false;
    }
    return false;
  }

}
