package mvc.model;

public interface Backtracking<P, C> {

    P firstDecisonPoint();

    P nextDecisionPoint(P point);

    P lastDecisionPoint();

    C firstChoice(P point);

    C nextChoice(C choice);

    C lastChoice(P point);

    boolean assignable(C choice, P point);

    void assign(C choice, P point);

    void deassign(C choice, P point);

    P previousDecisionPoint(P point);

    C lastChoiceAssignedTo(P point);

    void writeSolution(int nSol);

    default  void solve(){
        solve(Integer.MAX_VALUE);
    }

    default void solve(int maxSolutions) { // template method
        int nr_soluzione = 0;
        P ps = firstDecisonPoint();
        C c = firstChoice(ps);
        boolean backtrack = false, fine = false;
        do {
            // forward
            while (!backtrack && nr_soluzione < maxSolutions) {
                if (assignable(c, ps)) {
                    assign(c, ps);
                    if (ps.equals(lastDecisionPoint())) {
                        ++nr_soluzione;
                        writeSolution(nr_soluzione);
                        deassign(c, ps);
                        if (!c.equals(lastChoice(ps)))
                            c = nextChoice(c);
                        else
                            backtrack = true;
                    } else {
                        ps = nextDecisionPoint(ps);
                        c = firstChoice(ps);
                    }
                } else if (!c.equals(lastChoice(ps)))
                    c = nextChoice(c);
                else
                    backtrack = true;
            }// while( !backtrack ... )
            // backward
            fine = ps.equals(firstDecisonPoint())
                    || nr_soluzione == maxSolutions;
            while (backtrack && !fine) {
                ps = previousDecisionPoint(ps);
                c = lastChoiceAssignedTo(ps);
                deassign(c, ps);
                if (!c.equals(lastChoice(ps))) {
                    c = nextChoice(c);
                    backtrack = false;
                } else if (ps.equals(firstDecisonPoint()))
                    fine = true;
            }
        } while (!fine);
    }// risolvi

}
