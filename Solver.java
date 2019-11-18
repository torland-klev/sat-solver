
import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;
import java.util.Iterator;
import java.util.HashMap;
import java.io.IOException;

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

  //https://en.wikipedia.org/wiki/DPLL_algorithm
  private int dpll(PropositionalFormula formula){
    if (containsEmptyClause(formula)){
      return -1;
    }
    if (isConsistentSetOfLiterals(formula)){
      System.out.println(formula.getClauseSet());
      System.out.printf("Formula is a consistent set of literals.\n");
      return 1;
    }
    List<String> unitClauses = extractUnitClauses(formula);
    for (String literal : unitClauses){
      try{
        unitPropagate(literal, formula, (literal.length() == 2));
      } catch(Exception e){
        e.printStackTrace();
      }
    }
    List<String> pureLiterals = extractPureLiterals(formula);
    for (String literal : pureLiterals){
      try{
        unitPropagate(literal, formula, (literal.length() == 2));
      } catch(Exception e){
        e.printStackTrace();
      }
    }
    // Choose branching literal
    Iterator<String> iter = formula.getClauseSet().iterator();
    String firstClause = iter.next();
    String literal = (firstClause.charAt(0) == '-') ? firstClause.substring(1,2) : firstClause.substring(0,1);

    PropositionalFormula branching = formula.clone();
    try {
      unitPropagate(literal, branching, false);
    } catch(Exception e){
      e.printStackTrace();
    }
    int ret = dpll(branching);
    if (ret < 0){
      branching = formula.clone();
      try {
        unitPropagate("-" + literal, branching, true);
      } catch(Exception e){
        e.printStackTrace();
      }
      ret = dpll(branching);
    }

    return ret;
  }

  private boolean isConsistentSetOfLiterals(PropositionalFormula formula){
    List<String> clauseSet = formula.getClauseSet();
    List<Character> literals = new ArrayList<>();
    List<Character> negatedLiterals = new ArrayList<>();
    for (String clause : clauseSet){
      // Empty clause
      if (clause == null){
        continue;
      }
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


  /**
  * Sets the literal to be parameter negated in the formula, and
  * propagates the change through the formula. This method is destructive;
  * it makes changes to the formula.
  *
  * @param literal The literal to propagate on.
  * @param formula The formula to propagate on.
  * @param negated True if literal is negated, false if not.
  * @throws IOException If @param literal is not of length 1 (i.e., not a literal) and negated is false.
  * @see https://en.wikipedia.org/wiki/Unit_propagation
  */
  private void unitPropagate(String literal, PropositionalFormula formula, boolean negated) throws IOException{
    if (literal.length() != 1 && !negated){
      throw new IOException("Length of supplied literal parameter is not 1.");
    }
    if (literal.length() != 2 && negated){
      throw new IOException("Length of supplied negated literal parameter is not 2.");
    }

    List<String> clauses = formula.getClauseSet();
    for (ListIterator<String> iter = clauses.listIterator(); iter.hasNext();){

      String clause = iter.next();

      // Empty clause
      if (clause == null){
        continue;
      }

      if (clause.length() <= 1){
        // Either empty clause or unit clause
        continue;
      }
      if (clause.length() == 2 && negated && (clause.charAt(0) == '-')){
        // Negated unit clause
        continue;
      }
      boolean neg = negated;
      for (int i = 0; i < clause.length(); i++){
        if (clause.charAt(i) == '-'){
          if (negated){
            if (clause.charAt(i+1) == literal.charAt(1)){
              // Clause contains negated literal, clause can be removed.
              iter.remove();
              break;
            }
          }
          neg = !negated;
          continue;
        }
        if (clause.charAt(i) == literal.charAt(0)){
          // Clause contains non-negated literal, clause can be removed
          if (neg == negated){
            iter.remove();
            break;
          }
          // Clause contains negated literal, remove the literal and the negation from the clause
          String s = (i==0) ? clause.substring(i+1) : clause.substring(0, i-1) + clause.substring(i+1);
          iter.set((s.length() > 0) ? s : null);
        }
        if (negated && clause.charAt(i) == literal.charAt(1)){
          // Clause contains non-negated literal, remove the literal.
          String s = (i==1) ? clause.substring(i+1) : clause.substring(0, i) + clause.substring(i+1);
          iter.set((s.length() > 0) ? s : null);
        }
        neg = negated;
      }
    }
  }

  /**
  * This method extracts all pure literals of a formula.
  * Pure literals are literals that occur with only one polarity.
  * This method is non-destructive towards the formula.
  *
  * @param formula propositional formula to extract pure literals from
  * @return List<String> list of all pure literals in the formula
  * @see https://en.wikipedia.org/wiki/DPLL_algorithm
  */

  private List<String> extractPureLiterals(PropositionalFormula formula){
    List<String> clauses = formula.getClauseSet();
    HashMap<Character, Boolean> literals = new HashMap<>();
    List<Character> duplicates = new ArrayList<>();
    for (Iterator<String> iter = clauses.listIterator(); iter.hasNext(); ){
      String clause = iter.next();
      // Empty clause
      if (clause == null){
        continue;
      }
      for (int i = 0; i < clause.length(); i++){
        // Duplicate allready noted, skip
        if (duplicates.contains(clause.charAt(i))){
          continue;
        }
        Character literal;
        if (clause.charAt(i) == '-'){
          // Increment counter to get index to the literal
          i++;
          literal = clause.charAt(i);
          if (duplicates.contains(literal)){
            continue;
          }
          // Literal has been seen before
          if (literals.keySet().contains(literal)){
            // Literal has been seen negated before, nothing new; skip
            if (literals.get(literal)){
              continue;
            }
            // Literal has been non-negated before, but is negated now; add to duplicates.
            duplicates.add(literal);
            continue;
          }
          // New literal, add it
          literals.put(literal, true);
          continue;
        }

        literal = clause.charAt(i);
        // Literal has been seen before
        if (literals.keySet().contains(literal)){
          // Literal has been seen negated before; add to duplicates
          if (literals.get(literal)){
            duplicates.add(literal);
          }
          continue;
        }
        literals.put(literal, false);
      }
    }
    List<String> pureLiterals = new ArrayList<>();
    // The pure literas are the characters that are inn literals but not in duplicates.
    for (Character c : literals.keySet()){
      if (duplicates.contains(c)){
        continue;
      }
      pureLiterals.add((literals.get(c)) ? ("-" + c) : Character.toString(c));
    }
    return pureLiterals;
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
