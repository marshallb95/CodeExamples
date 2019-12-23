#include "Node.h"

Node::Node() {
	return;
}

void Node::setValue(int value) {
	this->value = value;
	return;
}

void Node::setName(string name) {
	ruleName = name;
}

string Node::getName() const {
	return ruleName;
}

void Node::initializeVisit() {
	visited = false;
}

void Node::addDependency(Node a) {
	dependencies.insert(a.getValue());
	return;
}

void Node::visit() {
	visited = true;
	return;
}

int Node::getValue() const {
	return value;
}

bool Node::getVisited() const {
	return visited;
}

set<int> Node::getDependencies() const {
	//cout << "Number of neighbors that will be returned is " << dependencies.size() << endl;
	return dependencies;
}

string Node::toString() {
	string str = "";
	str += "R" + to_string(value) + ":";
	if(dependencies.size() == 0) {
		str += "\n";
		return str;
	}
	else {
		set<int>::iterator it;
		for(it = dependencies.begin(); it != dependencies.end(); ++it) {
			str += "R" + to_string(*it) + ",";
		}
		str.pop_back();
		str += "\n";
		return str;
	}
}

