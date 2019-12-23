#include "Predicate.h"

Predicate::Predicate() {
	return;
}

string Predicate::predToString() {
	string str = Name +"(";
	for(unsigned int i = 0; i < Body.size(); i++) {

		str = str + Body[i].paramToString();

		if(i != Body.size()-1) {
			str = str + ",";
		}
	}
	str = str + ")";
	return str; //TODO
}

void Predicate::addName(string nameToAdd) {
	Name = nameToAdd;
	return; //TODO
}

void Predicate::addParameter(Parameter param) {
	Body.push_back(param);
	return; //TODO

}

string Predicate::getName() {
	return Name;
}

vector<Parameter> Predicate::getParameters() {
	return Body;
}

