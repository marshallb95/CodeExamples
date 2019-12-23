#pragma once
#include <iostream>
#include <vector>
#include <set>
using namespace std;
class SCC {
	private: 
		set<int>nodes;
		bool loop;
	public:
		SCC();
		void addNode(int node);
		vector<int> getNodes() const;
		void setLoop();
		int getSize() const;
		bool hasLoop() const;
		set<int> getComponents() const;
		string toString();
		string toString1();
		string toStringComponents();
};
