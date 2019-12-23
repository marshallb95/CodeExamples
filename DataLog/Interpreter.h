#pragma once
#include "Database.h"
#include "DataLogProgram.h"
#include "Graph.h"
#include "Node.h"
#include "SCC.h"
class Interpreter {
    private:
        DataLogProgram interpDatalog;
    	Database database;
	Graph dependencyGraph;
	Graph reverseGraph;
    public:
        Interpreter(DataLogProgram datalog);
        void makeDataBase();
	int getDatabaseCount();
	void evaluateQuery(Predicate query);
	Relation evaluatePredicate(Predicate bodyPred);
    	void evaluateAllQueries();
	Relation evaluateRule(Rule rule);
    	void evaluateRules();
	void buildDependencyGraph();
	void buildReverseDependencyGraph();
	vector<int> reverseDFSTree();
	vector<SCC> forwardDFSTree(vector<int> postorder);
	vector<SCC> findSCC();
	void evalSCC(vector<SCC> strongComp);

};
