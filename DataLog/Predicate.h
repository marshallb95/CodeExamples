#pragma once
#include "Parameter.h"
#include <string>
#include <vector>
#include <iostream>

class Predicate {

	private:
		string Name;
		vector<Parameter> Body; 
	public:
		Predicate();
		string predToString();
		void addName(string Name);
		void addParameter(Parameter param);
		string getName();
		vector<Parameter> getParameters();	
};
