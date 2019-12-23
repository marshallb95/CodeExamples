#pragma once
#include "Predicate.h"
#include "Parameter.h"
#include "Rule.h"
#include <set>

class DataLogProgram {
	private:
		vector<Predicate> schemeVec;
		vector<Predicate> factVec;
		vector<Predicate> queryVec;
		vector<Rule> ruleVec;
		set<string> domain;
	public:
		DataLogProgram();
		void printDataLog();
		void addScheme(Predicate scheme);
		void addFact(Predicate fact);
		void addQuery(Predicate query);
		void addRule(Rule rule);
		void addToDomain(string ruleString);
		vector<Predicate> getSchemes();
		vector<Predicate> getFacts();
		vector<Predicate> getQueries();
		vector<Rule> getRules();
};
