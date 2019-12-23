#pragma once
#include "Scheme.h"
#include "Tuple.h"
#include "string"
#include <iostream>
#include <set>
#include <map>

class Relation {
    private:
        string name;
        Scheme relationScheme;
        set<Tuple> relationTuples;
    public:
        Relation();
        void addTuple(Tuple tuple);
        set<Tuple> getTuples();
        string toString();
		void setName(string name);
		string getName();
		void setScheme(Scheme scheme);
		Scheme getScheme();
		Relation selectType1(string constant, set<int> indices);
		Relation selectType2(vector<int> indices);
		Relation project(vector<int> indicesToKeep);
		void ruleProject(set<string>, Scheme headScheme);
		Relation rename(string newName, vector<int> indices);
		int getSize();
		void join(Relation newRelation);
		Scheme combineSchemes(vector<string> att1, vector<string> att2, map<int,int> indexMap, vector<int> matchingInd);
		bool isJoinable(Tuple firstTuple, Tuple secondTuple, map<int,int> indexMap, vector<int> matchingInd);
		Tuple combineTuples(Tuple firstTuple, Tuple secondTuple, map<int,int> indexMap, vector<int> matchingInd);
		void unite(Relation resultRelation);
};
