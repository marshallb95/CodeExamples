#include "DataLogProgram.h"

DataLogProgram::DataLogProgram() {
	return;
}

void DataLogProgram::printDataLog() {
	cout << "Schemes(" << schemeVec.size() << "):" << endl;
	for(unsigned int i = 0; i < schemeVec.size(); i++) {
		cout << "  " + schemeVec[i].predToString() << endl;
	}
	cout << "Facts(" << factVec.size() << "):" << endl;
	for(unsigned int i = 0; i < factVec.size(); i++) {
		cout << "  " + factVec[i].predToString() + "." <<  endl;
	}
	cout << "Rules(" << ruleVec.size() << "):" << endl;
	for(unsigned int i = 0; i < ruleVec.size(); i++) {
		cout <<"  " + ruleVec[i].ruleToString() << endl;
	}
	cout << "Queries(" << queryVec.size() << "):" << endl;
	for(unsigned int i = 0; i < queryVec.size(); i++) {
		cout << "  " + queryVec[i].predToString()  + "?" << endl;
	}
	cout << "Domain(" << domain.size() << "):" << endl;
	set<string>::iterator it;
	for(it = domain.begin();it!= domain.end(); ++it) {
		cout << "  " << *it << endl;
	}
}

void DataLogProgram::addScheme(Predicate scheme) {
	schemeVec.push_back(scheme);
	return; 
}

void DataLogProgram::addFact(Predicate fact) {
	factVec.push_back(fact);
	return; 
}

void DataLogProgram::addQuery(Predicate query) {
	queryVec.push_back(query);
	return;
}

void DataLogProgram::addRule(Rule rule) {
	ruleVec.push_back(rule);
	return;
}


void DataLogProgram::addToDomain(string ruleString) {
	domain.insert(ruleString);
	return;
}

vector<Predicate> DataLogProgram::getSchemes() {
	return schemeVec;
}

vector<Predicate> DataLogProgram::getFacts() {
	return factVec;	
}

vector<Predicate> DataLogProgram::getQueries() {
	return queryVec;
}

vector<Rule> DataLogProgram::getRules() {
	return ruleVec;
}
