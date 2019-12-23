#include "Rule.h"

Rule::Rule() {
	return;
}

void Rule::addHead(Predicate head) {
	headPredicate = head;
	return;
}

void Rule::addToBody(Predicate member) {
	ruleBody.push_back(member);
	return;
}

string Rule::ruleToString() {
	string str = headPredicate.predToString();
	str = str + " :- ";
	for(unsigned int i = 0; i < ruleBody.size();i++) {
		str = str + ruleBody[i].predToString();
		if(i != ruleBody.size()-1) {
			str = str + ",";
		}
	}
	str = str + ".";
	return str;
}

vector<Predicate> Rule::getBody() {
	return ruleBody;
}

Predicate Rule::getHead() {
	return headPredicate;
}
