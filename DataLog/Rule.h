#pragma once
#include "Predicate.h"

class Rule {
	private:
		Predicate headPredicate;
		vector<Predicate> ruleBody;
	public:
		Rule();
		void addHead(Predicate head);
		void addToBody(Predicate member);
		string ruleToString();
		Predicate getHead();
		vector<Predicate> getBody();
};
