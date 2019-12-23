#pragma once
#include <iostream>
#include <set>
#include <string>
using namespace std;

class Node {
	private:
		string ruleName;
		int value;
		set<int> dependencies;
		bool visited;
	public:
		Node();
		void setName(string name);
		void setValue(int value);
		void addDependency(Node a);
		void initializeVisit();
		void visit();
		int getValue() const;
		bool getVisited() const;
		string getName() const;
		set<int> getDependencies() const;
		string toString();
};	
