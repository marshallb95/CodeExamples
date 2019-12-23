#include "SCC.h"

SCC::SCC() {
	loop = false;
	return;
}

void SCC::addNode(int node) {
	nodes.insert(node);
}

void SCC::setLoop() {
	loop = true;
}

int SCC::getSize() const {
	return nodes.size();
}

bool SCC::hasLoop() const {
	return loop;
}

set<int> SCC::getComponents() const{
	return nodes;
}

string SCC::toString() {
	string str = "";
	str += "Size: " + to_string(nodes.size()) + "\n";
	str += "Loops?: ";
	if(loop == true) {
		str += "True\n";
	}
	else {
		str += "False\n";
	}
	str += "Components: ";
	set<int>::iterator it;
	for(it = nodes.begin(); it != nodes.end(); ++it) {
		str += to_string(*it) + " ";
	}
	str += "\n";
	return str;
}

string SCC::toString1() {
	string str = "";
	str += "SCC: ";
	set<int>::iterator it;
	for(it = nodes.begin(); it != nodes.end(); ++it) {
		str += "R" + to_string(*it) + ",";
	}
	str.pop_back();
	str += "\n";
	return str;
}

string SCC::toStringComponents() {
	string str = "";
	set<int>::iterator it;
	for(it = nodes.begin(); it != nodes.end(); ++it) {
		str += "R" + to_string(*it) + ",";
	}
	str.pop_back();
	str += "\n";
	return str;
}
