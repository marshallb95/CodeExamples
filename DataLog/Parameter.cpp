#include "Parameter.h"

Parameter::Parameter() {
	return;
}

string Parameter::paramToString() {
	string str = "";
	for(unsigned int i = 0; i < paramVec.size();i++) {
		str = str + paramVec[i];
	}
	return str;//TODO
}

void Parameter::addToParameter(string token) {
	paramVec.push_back(token);
	return;
}

vector<string> Parameter::getString() {
	return paramVec;
}

bool Parameter::getIsConstant() {
	return isConstant;
}

void Parameter::setIsConstant(bool constant) {
	isConstant = constant;
}