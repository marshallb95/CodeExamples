#pragma once
#include <map>
#include "Scheme.h"
#include "Tuple.h"
#include "Relation.h"
#include <string>
using namespace std;

class Database: public map<string, Relation> {
	private:
		int getCount();	
};
