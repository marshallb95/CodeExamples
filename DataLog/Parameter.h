#pragma once
#include <string>
#include <vector>
#include <iostream>
#include <sstream>

using namespace std;

class Parameter {
	private:
		bool isConstant;
		vector<string> paramVec; //Can be an ID, STRING, or EXPRESSION 
	public:
		Parameter();
		string paramToString();
		void addToParameter(string token);
		vector<string> getString();
		bool getIsConstant();
		void setIsConstant(bool constant);
};
