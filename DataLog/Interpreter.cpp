#include "Interpreter.h"
#include <algorithm>
using namespace std;

Interpreter::Interpreter(DataLogProgram datalog) {
	interpDatalog = datalog;
}

void Interpreter::makeDataBase() {
	//////cout << "In makeDataBase()" << endl;
   	vector<Predicate> facts = interpDatalog.getFacts();
   	vector<Predicate> schemes = interpDatalog.getSchemes();
   	//For each of the schemes, make a scheme, and a relation with that scheme
	for(unsigned int i = 0; i < schemes.size(); ++i) {
		Scheme scheme;
		vector<Parameter> parameters = schemes[i].getParameters();
		for(unsigned int j = 0; j < parameters.size(); ++j) {
			string attribute = parameters[j].paramToString();
			scheme.addAttribute(attribute);
		}
		scheme.setName(schemes[i].getName());
		Relation relation;
		relation.setName(schemes[i].getName());
		relation.setScheme(scheme);
		database[schemes[i].getName()] = relation;
	}
	for(unsigned int i = 0; i < facts.size(); ++i) {
		Tuple tuple;
		vector<Parameter> parameters = facts[i].getParameters();
		for(unsigned j = 0; j < parameters.size(); ++j) {
			string attributeFact = parameters[j].paramToString();
			tuple.push_back(attributeFact);
		}
		Relation relation = database[facts[i].getName()];
		relation.addTuple(tuple);
		database[facts[i].getName()] = relation;
	}
	return;
}

int Interpreter::getDatabaseCount() {
	////cout << "In getCount" <<  endl;
	Database::iterator it;
	set<Tuple>::iterator t;
	int tot = 0;
	for(it = database.begin(); it != database.end(); ++it) {
		//////cout << "In outer loop" << endl;
		////cout << "Relation is " << it->first << " and size is " << it->second.getTuples().size() << endl;
		tot += it->second.getTuples().size();
	}
	////cout << "Left getcount" << endl;
	return tot;
}
void Interpreter::evaluateQuery(Predicate query) {
	//////cout << "In evaluate Query" << endl;
	vector<Parameter> parameters = query.getParameters();
	map <string,set<int> > constIndices;
	map<string,vector<int> > variableIndices;
	vector<string> consts;
	vector<string> variables;
	Relation relation = database[query.getName()];
	
	//output what the query is
	cout << query.predToString() << "? ";
	
	//For each of the Parameters in the query assign whether it's a variable or a constant
	for(unsigned int i = 0; i < parameters.size(); ++i) {
		if(parameters[i].getIsConstant() == true) {
			if(constIndices.find(parameters[i].paramToString()) != constIndices.end()) {
				set<int> indices = constIndices[parameters[i].paramToString()];
				indices.insert(i);
				constIndices[parameters[i].paramToString()] = indices;
			}
			else {
				set<int> indices;
				indices.insert(i);
			constIndices[parameters[i].paramToString()] = indices;
			consts.push_back(parameters[i].paramToString());
			}
		}
		else {
			if(variableIndices.find(parameters[i].paramToString()) != variableIndices.end()) {
				vector<int> indices = variableIndices[parameters[i].paramToString()];
				indices.push_back(i);
				variableIndices[parameters[i].paramToString()] = indices;
			}
			else {
				vector<int> indices;
				indices.push_back(i);
				variableIndices[parameters[i].paramToString()] = indices;
				variables.push_back(parameters[i].paramToString());
			}
		}
	}
	//perform all the select types one
	for(unsigned int i = 0; i < consts.size(); ++i) {
		relation = relation.selectType1(consts[i],constIndices[consts[i]]);
	}
	//peform all the select types two
	for(unsigned int i = 0; i < variables.size(); ++i) {
		if(variableIndices[variables[i]].size() >= 2) {
			relation = relation.selectType2(variableIndices[variables[i]]);
		}
	}

	//perform all the renames
	for(unsigned int i = 0; i < variables.size(); ++i) {
		relation = relation.rename(variables[i], variableIndices[variables[i]]);
	}
	
	//If there are no variables
	if(variables.size() == 0) {
		int relSize = relation.getSize();
		//If there are no tuples in the relation
		if(relSize == 0) {
			cout << "No" << endl;
		}
		//IF there are tuples in the relation
		else {
			cout << "Yes(" + to_string(relSize) + ")" << endl;
		}
		return;
	}
	//Keep track of which columns need to be removed
	vector<int> toRemove;
	for(unsigned int i = 0; i < consts.size(); ++i) {
		for(auto t: constIndices[consts[i]]) {
			toRemove.push_back(t);
		}
	}
	for(unsigned int i = 0; i < variables.size(); ++i) {
		if(variableIndices[variables[i]].size() >= 2) {
			vector<int> vec = variableIndices[variables[i]];
			for(unsigned int j = 1; j < vec.size(); ++j) {
				toRemove.push_back(vec[j]);
			}
		}
	}
	//Sort indexes
	sort(toRemove.begin(), toRemove.end());
	//project
	relation = relation.project(toRemove);
	int Size = relation.getSize();
	if(Size == 0) {
		cout << "No" << endl;
	}
	else {
		cout << "Yes(" + to_string(Size) + ")" << endl;
		cout << relation.toString();
	}
	return;
}

void Interpreter::evaluateAllQueries() {
	//////cout << "In eval all" << endl;
	cout << "Query Evaluation" << endl;
	vector<Predicate> queries = interpDatalog.getQueries();
	for(unsigned int i = 0; i < queries.size(); ++i) {
		evaluateQuery(queries[i]);
	}
	return;
}

void Interpreter::evaluateRules() {
	//////cout << "In evaluateRules()" << endl;
	vector<Rule> rules = interpDatalog.getRules();
	Relation relation;
	Relation databaseRelation;
	int precount = 0;
	int postcount = 0;
	int passes = 0;
	if(rules.size() != 0) {
		cout <<  "Rule Evaluation" << endl;
	}
	/*////cout << "database before evaluating is" << endl;
	Database::iterator it;
	for(it=database.begin(); it != database.end(); ++it)
		////cout << it->first << " " << it->second.toString() << endl;
		*/
	do {
		passes += 1;
		////cout << "Entered do while" << endl;
		precount = getDatabaseCount();
		////cout << "pre database count is " << precount << endl;
		for(unsigned int i = 0; i < rules.size();++i) {
			cout << rules[i].ruleToString() << endl;
			relation = evaluateRule(rules[i]);
			databaseRelation = database[relation.getName()];
						
			//////cout << "relation pulled from database is" << databaseRelation.toString() << endl;
			databaseRelation.unite(relation);
			database[relation.getName()] = databaseRelation;
		}
		postcount = getDatabaseCount();
		////cout << "post database count is " <<  postcount << endl;

	}	
	while(precount != postcount);
	if(rules.size() != 0) {
		cout << endl;
		cout << "Schemes populated after " + to_string(passes) + " passes through the Rules." << endl;
		cout << endl;
		cout << "Query Evaluation" << endl;
	}
	return;
}

Relation Interpreter::evaluateRule(Rule rule) {
	//////cout << "In evaluate rule" << endl;
	Predicate head = rule.getHead();
	vector<Predicate> ruleBody = rule.getBody();
	vector<Relation> ruleRelations;
	string headName = head.getName();
	set<string> schemeAttr;
	Scheme headScheme;
	vector<Parameter> headParam = head.getParameters();
	for(unsigned int i = 0; i < headParam.size(); ++i) {
		schemeAttr.insert(headParam[i].paramToString());
		headScheme.addAttribute(headParam[i].paramToString());
	}
	
	for(unsigned int i = 0; i < ruleBody.size(); ++i) {
		Relation relation;
		relation = evaluatePredicate(ruleBody[i]);
		//////cout << "relation after being evaluated is " << endl;
		//////cout << relation.toString() << endl;
		ruleRelations.push_back(relation);
	}
	
	int size = ruleRelations.size();
	if(size == 1) {
		ruleRelations[0].ruleProject(schemeAttr,headScheme);
		ruleRelations[0].setName(headName);
		//////cout << "Relations in size 1" << endl;
		//////cout << ruleRelations[0].toString();
		return ruleRelations[0];
	}
	else {
		Relation firstRelation;
		firstRelation = ruleRelations[0];
		for(unsigned int i = 1; i < ruleRelations.size();++i) {
			firstRelation.join(ruleRelations[i]);
		//	////cout << "Relation after join is " << endl;
		//	////cout << firstRelation.toString() << endl;
		}
		firstRelation.ruleProject(schemeAttr,headScheme);
		firstRelation.setName(headName);
		//////cout << "Relation after ruleProject is " << endl;
		//////cout << firstRelation.toString() << endl;
		return firstRelation;
	}
}

Relation Interpreter::evaluatePredicate(Predicate bodyPred) {
	////cout << "In evaluate Predicate" << endl;
	vector<Parameter> parameters = bodyPred.getParameters();
	map <string,set<int> > constIndices;
	map<string,vector<int> > variableIndices;
	vector<string> consts;
	vector<string> variables;
	Relation relation = database[bodyPred.getName()];
	//output what predicate in rule is
	//////cout << bodyPred.predToString() << "? ";
	//Find indexes for constants and variables
	for(unsigned int i = 0; i < parameters.size(); ++i) {
		if(parameters[i].getIsConstant() == true) {
			//////cout << "Parameter found as constant is " << parameters[i].paramToString() << endl;
			if(constIndices.find(parameters[i].paramToString()) != constIndices.end()) {
				//////cout << "At index " << i << "this variable has been found before in the predicate" << endl;
				set<int> indices = constIndices[parameters[i].paramToString()];
				indices.insert(i);
				constIndices[parameters[i].paramToString()] = indices;
			}
			else {
				//////cout << "Index has not been found before" << endl;
				set<int> indices;
				indices.insert(i);
				constIndices[parameters[i].paramToString()] = indices;
				consts.push_back(parameters[i].paramToString());
			}
		}
		else {
			//////cout << "Parameter found as variable is " << parameters[i].paramToString() << endl;
			if(variableIndices.find(parameters[i].paramToString()) != variableIndices.end()) {
				//////cout << "Variable at index " << i << "has been found before" << endl;
				vector<int> indices = variableIndices[parameters[i].paramToString()];
				indices.push_back(i);
				variableIndices[parameters[i].paramToString()] = indices;
			}
			else {
				//////cout << "Variable has not been found before" << endl;
				vector<int> indices;
				indices.push_back(i);
				variableIndices[parameters[i].paramToString()] = indices;
				variables.push_back(parameters[i].paramToString());
			}
		}
	}
	//Select type1 on all constants
	for(unsigned int i = 0; i < consts.size(); ++i) {
		relation = relation.selectType1(consts[i],constIndices[consts[i]]);
	}
	//////cout << "Relation after type1 project is" << endl;
	//////cout << relation.toString();
	//select type2 on all variables
	for(unsigned int i = 0; i < variables.size(); ++i) {
		if(variableIndices[variables[i]].size() >= 2) {
			relation = relation.selectType2(variableIndices[variables[i]]);
		}
	}
	//////cout << "Relation after type2 project is" << endl;
	//Rename variables accordingly
	for(unsigned int i = 0; i < variables.size(); ++i) {
		relation = relation.rename(variables[i], variableIndices[variables[i]]);
	}
	//////cout << "Relation after rename is" << endl;
	//If no variables, done
	if(variables.size() == 0) {
		int relSize = relation.getSize();
		if(relSize == 0) {
	//		////cout << "No" << endl;
		}
		else {
	//		////cout << "Yes(" + to_string(relSize) + ")" << endl;
	//		////cout << relation.toString() << endl;
		}
		////cout << "Evaluated relation is in Evaluate Predicate is " << relation.getName() << " " << relation.toString() << endl;
		return relation;
	}
	//If variables, find where duplicates need to be removed, along with constants
	vector<int> toRemove;
	for(unsigned int i = 0; i < consts.size(); ++i) {
		for(auto t: constIndices[consts[i]]) {
			toRemove.push_back(t);
		}
	}
	for(unsigned int i = 0; i < variables.size(); ++i) {
		if(variableIndices[variables[i]].size() >= 2) {
			vector<int> vec = variableIndices[variables[i]];
			for(unsigned int j = 1; j < vec.size(); ++j) {
				toRemove.push_back(vec[j]);
			}
		}
	}
	sort(toRemove.begin(), toRemove.end());
	
	//project all columns that aren't duplicate variables or constants
	relation = relation.project(toRemove);
	int Size = relation.getSize();
	if(Size == 0) {
	//	////cout << "No" << endl;
	}
	else {
	//	////cout << "Yes(" + to_string(Size) + ")" << endl;
	//	////cout << relation.toString() << endl;
	}
	////cout << "Relation after evaluatePredicate is " << relation.getName() << " " << relation.toString() << endl;
	return relation;
}

void Interpreter::buildDependencyGraph() {
	vector<Rule> rules = interpDatalog.getRules();
	Node node;
	vector<Predicate> body;
	string name;
	set<int> neighbors;
	for(unsigned int i = 0; i < rules.size();++i) {
		node.setValue(i);
		node.setName(rules[i].getHead().getName());
		dependencyGraph.addNode(node);
	}
	//cout << dependencyGraph.toString() << endl;
	//for each rule
	for(unsigned int i = 0; i < rules.size();++i) {
		body = rules[i].getBody();//get body predicates
		for(unsigned int j = 0; j < body.size(); ++j) {//for each predicate in body
			name = body[j].getName();//get name
			//cout << "Name is body is " << name << endl;
			neighbors = dependencyGraph.getNodes(name);
			for(auto t: neighbors) {
				dependencyGraph.addEdge(i,t);
			}
		}
	}
	cout << "Dependency Graph" << endl;
	cout << dependencyGraph.toString() << endl;	
	return;
}

void Interpreter::buildReverseDependencyGraph() {
	vector<Rule> rules = interpDatalog.getRules();
	Node node;
	vector<Predicate> body;
	string name;
	set<int> neighbors;
	for(unsigned int i = 0; i < rules.size();++i) {
		node.setValue(i);
		node.setName(rules[i].getHead().getName());
		reverseGraph.addNode(node);
	}
	//cout << reverseGraph.toString() << endl;
	//for each rule
	for(unsigned int i = 0; i < rules.size();++i) {
		body = rules[i].getBody();//get body predicates
		for(unsigned int j = 0; j < body.size(); ++j) {//for each predicate in body
			name = body[j].getName();//get name
			//cout << "Name is body is " << name << endl;
			neighbors = reverseGraph.getNodes(name);
			for(auto t: neighbors) {
				reverseGraph.addEdge(t,i);
			}
		}
	}
	//cout << "Reverse Dependency Graph:" << endl;
	//cout << reverseGraph.toString() << endl;	
	return;
}
//Will need to be revised
vector<int> Interpreter::reverseDFSTree() {
	vector<int> order;
	vector<int> postorder;
	for(int i = 0; i < reverseGraph.getSize(); ++i) {
	       order.push_back(i);
	}	
	postorder = reverseGraph.DFSTree(order, true);
/*	cout << "Post order after traversing reverse graph is:" << endl;
	for(unsigned int i = 0; i < postorder.size(); ++i) {
		cout << postorder[i] << " ";
	}
	cout << endl;*/
	return postorder;
}
//Will get post-order from reverseDFSTRee
vector<SCC> Interpreter::forwardDFSTree(vector<int> postorder) {
	vector<SCC> components;	
	components = dependencyGraph.DFSTreeSCC(postorder);
	/*cout << "Strongly connected component order is" << endl;
	for(unsigned int i = 0; i < components.size(); ++i) {
		cout << components[i].toString() << endl;
	}
	cout << endl;*/
	return components;
}

vector<SCC> Interpreter::findSCC() {
	vector<SCC> strong;
	vector<int> postorder;
	postorder = reverseDFSTree();
	reverse(postorder.begin(), postorder.end());
	/*cout << "reverse order is: ";
	for(unsigned int i = 0; i < postorder.size(); ++i) {
		cout << postorder[i] << " ";
	}
	cout << endl;*/
	strong = forwardDFSTree(postorder);
	return strong;
}

void Interpreter::evalSCC(vector<SCC> strongComp) {
	set<int> nodeRule;
	vector<Rule> rules = interpDatalog.getRules();
	Relation relation;
	Relation databaseRelation;
	cout << "Rule Evaluation" << endl;
	for(unsigned int i = 0; i < strongComp.size(); ++i) {
		if(strongComp[i].getSize() == 1) {
			cout << strongComp[i].toString1();
			nodeRule = strongComp[i].getComponents();
			if(strongComp[i].hasLoop() == true) {
				int passes = 0;
				int precount = 0;
				int postcount = 0;
				do {
					passes += 1;
					precount = getDatabaseCount();
					for(auto t: nodeRule) {
						cout << rules[t].ruleToString() << endl;
						relation = evaluateRule(rules[t]);
						databaseRelation = database[relation.getName()];
						databaseRelation.unite(relation);
						database[relation.getName()] = databaseRelation;
					}
					postcount = getDatabaseCount();
				}
				while(precount != postcount);
				cout << passes << " passes: " << strongComp[i].toStringComponents();
			}
			else {//only one pass through rule
				for(auto t: nodeRule) {
					cout << rules[t].ruleToString() << endl;
					relation = evaluateRule(rules[t]);
					databaseRelation = database[relation.getName()];
					databaseRelation.unite(relation);
					database[relation.getName()] = databaseRelation;
				}
				cout << "1 passes: " << strongComp[i].toStringComponents();
			}
		}
		else if(strongComp[i].getSize() > 1) {//if multiple nodes in connected component
			//fixed point algorithm
			nodeRule = strongComp[i].getComponents();
			cout << strongComp[i].toString1();
			int passes = 0; 
			int precount = 0; 
			int postcount = 0;
			do {
				passes += 1;
				precount = getDatabaseCount();
				for(auto t: nodeRule) {
					cout << rules[t].ruleToString() << endl;
					relation = evaluateRule(rules[t]);
					databaseRelation = database[relation.getName()];
					databaseRelation.unite(relation);
					database[relation.getName()] = databaseRelation;
				}
				postcount = getDatabaseCount();
			}
			while(precount != postcount);
			cout << passes << " passes: " << strongComp[i].toStringComponents();
		}
		else {
			//size must be zero
			continue;
		}
	}
	cout << endl;
	//build and return SCC Components
	return;
}


