#include "Relation.h"


Relation::Relation() {
	return;
}

void Relation::setName(string Name) {
	name = Name;
}

void Relation::setScheme(Scheme scheme) {
	relationScheme = scheme;
}

Scheme Relation::getScheme() {
	return relationScheme;
}

string Relation::getName() {
	return name;
}
void Relation::addTuple(Tuple tuple) {
    relationTuples.insert(tuple);
}

set<Tuple> Relation::getTuples() {
	return relationTuples;
}
string Relation::toString() {
	///////cout << "In toString" << endl;
	Tuple mytuple;
	string str = "";
	set<Tuple>::iterator it;
	vector<string> attributes = relationScheme.getAttributes();
//	///cout << "Attributes # is " << attributes.size() << endl;
	for(unsigned int i = 0; i < attributes.size(); ++i) {
//		///cout << "Attribute is" << endl;
//		///cout << attributes[i] << endl;
	}
	for(auto t: relationTuples) {
//		///cout << "entered into auto part of loop" << endl;
//		///cout << "Tuples is" << endl;
//		///cout << "T.size() is " << t.size() << endl;
//		///cout << "t.size() is " << t.size() << endl;
		for(unsigned int i= 0; i < t.size(); ++i) {
//			///cout << "entered into inner loop" << endl;
//			///cout << "i is " << i << endl;
			if(i == 0) {
				str += "  ";
			}
			if(i != t.size()-1) {
//				///cout << "Entered into if statement" << endl;
				str += attributes[i] + "=" + t[i] + ", ";
			}
			else  {
//				///cout << "entered into else statement" << endl;
				str += attributes[i] + "=" + t[i];
			}
		}
		str += "\n";
	}
    return str;
}

Relation Relation::selectType1(string constant, set<int> indices) {
	/////cout << "In Select Type 1" << endl;
	Relation newRelation;
	newRelation.name = name;
	newRelation.relationScheme = relationScheme;
	set<int>::iterator it;
	bool AllTrue;
	for(auto t: relationTuples) {
		AllTrue = true;
		for(it = indices.begin(); it != indices.end(); ++it) {
			if(t[*it] != constant) {
				/////cout << "values not equal " << t[*it] << " and " << constant << endl; 
				AllTrue = false;
			}
		}
		if(AllTrue == true) {
			newRelation.addTuple(t);
		}
	}
	return newRelation;
}

Relation Relation::selectType2(vector<int> indices) {
	/////cout << "In select Type2" << endl;
	Relation newRelation;
	newRelation.name = name;
	newRelation.relationScheme = relationScheme;
	bool allSame;
	int first = indices[0];
	for(auto t: relationTuples) {
		allSame = true;
		for(unsigned int i = 1; i < indices.size(); ++i) {
			if(t[first] != t[indices[i]]) {
				allSame = false;
			}
		}
		if(allSame  == true) {
			newRelation.addTuple(t);
		}
	}


	return newRelation;
}

Relation Relation::project(vector<int> indicesToRemove) {
	/////cout << "In project" << endl;
	Relation newRelation;
	newRelation.name = name;
	newRelation.relationScheme = relationScheme;
	newRelation.relationTuples = relationTuples;
        int i = 0;
    //Go through each index that needs to be removed   
	while(indicesToRemove.size() != 0) {
		/////cout << "Entered while loop in project"  << endl;
		if(i == indicesToRemove[0]) {
			newRelation.relationScheme.removeAttribute(i);
			vector<Tuple> alterVec;
			//Remove tuples from set and put into vector
			for(auto t: newRelation.relationTuples) {
				auto t1 = t;
				alterVec.push_back(t1);
				newRelation.relationTuples.erase(t1);
			}
			//Alter tuples, then put back into set
			for(unsigned int z = 0; z < alterVec.size(); ++z) {
				alterVec[z].erase(alterVec[z].begin()+i);
				newRelation.relationTuples.insert(alterVec[z]);
			}
			//remove index from vector
			indicesToRemove.erase(indicesToRemove.begin());
			for(unsigned int j = 0; j < indicesToRemove.size(); ++j) {
				//decrement each index by one, since one column has been deleted
				indicesToRemove[j] -= 1;
			}
			//shift pointer accordingly
			i -= 1;
		}
		i += 1;
	}
	return newRelation;
}

Relation Relation::rename(string newName, vector<int> indices) {
	/////cout << "In rename" << endl;
	Relation newRelation;
	newRelation.name = name;
	newRelation.relationScheme = relationScheme;
	newRelation.relationTuples = relationTuples;
	vector<string> attributes = relationScheme.getAttributes();
	//If names don't match, change it to match
	for(unsigned int i = 0; i < indices.size(); ++i) {
		if(attributes[indices[i]] != newName) {
			newRelation.relationScheme.changeAttribute(newName, indices[i]);
		}
	}
	return newRelation;
}

int Relation::getSize() {
	return relationTuples.size();
}
//NOTE THESE METHODS MAY BE MOVED TO A DIFFERENT CLASS

void Relation::join(Relation newRelation) {
	/////cout << "In join" << endl;
	vector<string> att1 = relationScheme.getAttributes();
	vector<string> att2 = newRelation.getScheme().getAttributes();
	map<int,int> indexMap;
	vector<int> matchingInd;
	//find where attributes match in each scheme, if any
	for(unsigned int i = 0; i < att1.size();++i) {
		for(unsigned int j = 0; j < att2.size();++j) {
			if(att1[i] == att2[j]) {
				///cout << "attributes" << att1[i] << " and " << att2[j] << " match. Corresponding indices are" << i << " and " << j << endl;
				indexMap[i] = j;
				matchingInd.push_back(i);
			}
		}
	}
	Scheme combinedScheme;
	combinedScheme = combineSchemes(att1, att2, indexMap, matchingInd);
	set<Tuple> tuples;
	vector<Tuple> firstTuples;
	vector<Tuple> secondTuples;
	for(auto t: relationTuples) {
		auto t1 = t;
		firstTuples.push_back(t1);
	}
	for(auto t: newRelation.getTuples()) {
		auto t1 = t;
		secondTuples.push_back(t1);
	}
	Tuple tuple;
	for(unsigned int i = 0; i < firstTuples.size(); ++i) {
		for(unsigned int j = 0; j < secondTuples.size(); ++j) {
			if(isJoinable(firstTuples[i], secondTuples[j],indexMap,matchingInd)) {
				tuple = combineTuples(firstTuples[i],secondTuples[j],indexMap,matchingInd);
				tuples.insert(tuple);
			}
		}
	}
	relationScheme = combinedScheme;
	relationTuples = tuples;
	return; //TODO 
}
Scheme Relation::combineSchemes(vector<string> att1, vector<string> att2, map<int,int> indexMap, vector<int> matchingInd) {
	//cout << "In combine Schemes" << endl;
	//cout << "Schemes being joined are";
	for(unsigned int i = 0; i < att1.size(); ++i) {
		//cout << " " << att1[i];
	}
	//cout << " and ";
	for(unsigned int i = 0; i < att2.size(); ++i) {
		//cout << " "  << att2[i];
	}
	//cout << endl;
	Scheme scheme;
	if(indexMap.size() == 0) {
		//cout << "no indices correspond, so add first scheme, then second scheme" << endl << endl;
		for(unsigned int i = 0; i < att1.size();++i) {
			scheme.addAttribute(att1[i]);
		}
		for(unsigned int i = 0; i < att2.size();++i) {
			scheme.addAttribute(att2[i]);
		}
	}
	else {
		//cout << "Matches exist. They are: ";
		int index;
		for(unsigned int i = 0; i < matchingInd.size();++i) {
			//cout << matchingInd[i] << " and ";
			index = indexMap[matchingInd[i]];
			//cout << index << endl << endl;
			att2.erase(att2.begin()+index);
			//check if following indices still correspond correctly after deleting
			for(unsigned int j = i+1;j < matchingInd.size(); ++j) {
				//if they don't
				if(att1[matchingInd[j]] != att2[indexMap[matchingInd[j]]]) {
					//go through the second scheme and find the correct variable
					for(unsigned int k = 0; k < att2.size(); ++k) {
						if(att2[k] == att1[matchingInd[j]]) {
							matchingInd[j] = k;
						}
					}
				}
			}
		}
		

		for(unsigned int i = 0; i < att1.size();++i) {
			scheme.addAttribute(att1[i]);
		}
		for(unsigned int i = 0; i < att2.size();++i) {
			scheme.addAttribute(att2[i]);
		}
	}
	//cout << "New combined scheme is" << endl;
	vector<string> attr = scheme.getAttributes();
	for(unsigned int i = 0; i < attr.size(); ++i) {
		//cout << attr[i] << " ";
	}
	//cout << endl;
	return scheme;
}

bool Relation::isJoinable(Tuple firstTuple, Tuple secondTuple,map<int,int> indexMap, vector<int> matchingInd) {
	bool joinable = true;
	
	if(indexMap.size() == 0) {
		///cout << "No corresponding indices, so joinable" << endl << endl;
		return joinable;
	}
	else {
		for(unsigned int i = 0; i < matchingInd.size();++i) {
			if(firstTuple[matchingInd[i]] != secondTuple[indexMap[matchingInd[i]]]) {
				///cout << "Tuples were found not to match at corresponding indices. Values were: ";
				///cout << firstTuple[i] << " and " << secondTuple[indexMap[i]] << endl << endl;
				joinable = false;
				break;
			}
		}
		if(joinable == true) {
			///cout << "tuples were found to be the same at corresponding indices. Tuples are: " << endl;
			for(unsigned int i = 0; i < firstTuple.size(); ++i) {
				///cout << firstTuple[i] << " ";
			}
			///cout << endl;
			for(unsigned int i = 0; i < secondTuple.size(); ++i) {
				///cout << secondTuple[i] << " ";
			}
			///cout << endl;
		}

		return joinable;
	}
}

Tuple Relation::combineTuples(Tuple firstTuple, Tuple secondTuple,map<int,int> indexMap, vector<int> matchingInd) {
	if(indexMap.size() != 0) {
		//For each of the corresponding matching indices in the first tuple
		for(unsigned int i = 0; i < matchingInd.size(); ++i) {
			//cout << "erasing value at matching index. Value wanted to erase is" << firstTuple[matchingInd[i]] << endl;
			//cout << "After correction, value erased is " << secondTuple[indexMap[matchingInd[i]]] << endl;
			secondTuple.erase(secondTuple.begin()+indexMap[matchingInd[i]]);
			//After erasing, if other corresponding variables no longer align, refind corresponding index
			for(unsigned int j = i+1; j < matchingInd.size(); ++j) {
				//if not match like it should be
				if(firstTuple[matchingInd[j]] != secondTuple[indexMap[matchingInd[j]]]) {
					//cout << "first and second tuple no longer match at given spots. First tuple is " << firstTuple[matchingInd[j]] << " and second Tuple is " << secondTuple[indexMap[matchingInd[j]]] << endl;
					//go through second tuple and refind match
					for(unsigned int k = 0; k < secondTuple.size();++k) {
						if(secondTuple[k] == firstTuple[matchingInd[j]]) {
							//cout << "After searching through again, correct values were found at indexes " << k << ", " << matchingInd[j] << " with values " << secondTuple[k] << " and " << firstTuple[matchingInd[j]] << endl;
							indexMap[matchingInd[j]] = k;
						}
					}
				}
			}	
		}
	}
	firstTuple.insert(firstTuple.end(),secondTuple.begin(),secondTuple.end());
	//cout << "Tuple after being combined is ";
	for(unsigned int i = 0; i < firstTuple.size(); ++i) {
		//cout << firstTuple[i] << " ";
	}
	//cout << endl << endl;
	return firstTuple;
}

void Relation::ruleProject(set<string> schemeAttr, Scheme headScheme) {
	///cout << "In ruleProject" << endl;
	//cout << "Relation that is being projected is " << endl;
	//cout << toString() << endl;
	vector<Tuple> tuples;
	vector<string> attributesHead = headScheme.getAttributes();
	vector<string> attributesBody = relationScheme.getAttributes();
	vector<int> indicesRemove;
	map<string,int> indexTracker;
	///cout << "Relation size before is " << relationTuples.size() << endl;
	for(auto t: relationTuples) {
		auto t1 = t;
		tuples.push_back(t1);
		relationTuples.erase(t1);
	}
	///cout << "Relation size after is " << relationTuples.size() << endl;
	//cout << "tuples size is " << tuples.size() << endl;
	//For debugging
	//cout << "Head scheme is ";
	for(unsigned int i = 0; i < attributesHead.size(); ++i) {
		//cout << " " << attributesHead[i];
	}
	//cout << endl;
	//cout << "Body scheme is ";
	for (unsigned int i = 0; i < attributesBody.size(); ++i) {
		//ncout << " " << attributesBody[i];
	}
	//cout << endl;
	for(unsigned int i = 0; i < attributesBody.size();++i) {
		if(schemeAttr.find(attributesBody[i]) == schemeAttr.end()) {
			//cout << "attribute not found in head scheme: " << attributesBody[i] << endl;
			//cout << "Index to remove is " << i << endl;;
			indicesRemove.push_back(i);
		}
	}
	
	for(unsigned int t = 0; t < indicesRemove.size(); ++t) {
		//cout << "in auto loop, index to remove is " << indicesRemove[t] << endl;
		//cout << "Attribute that will be removed is " << attributesBody[indicesRemove[t]] << endl;
		attributesBody.erase(attributesBody.begin()+indicesRemove[t]);
		for(unsigned int i =0; i < tuples.size();++i) {
			///cout << "Value in tuple that will be erased is " << tuples[i][t] << endl;;
			tuples[i].erase(tuples[i].begin()+indicesRemove[t]);
		}
		for(unsigned int i = t+1; i<indicesRemove.size();++i) {
			indicesRemove[i] -= 1;
		}
	}
	for(unsigned int i = 0; i < attributesBody.size();++i) {
		//cout << "after erasing, attributes is " << attributesBody[i] << " with index " << i << endl;
		indexTracker[attributesBody[i]] = i;
	}
	string tempVal;
	int ind;
	//cout << "aligning schemes and tuples " << endl;
	for(unsigned int i = 0; i < attributesHead.size();++i) {
		if(attributesBody[i] != attributesHead[i]) {
			//cout << "Body attribute does not equal head attribute " << attributesBody[i] << " " << attributesHead[i] << endl;
			ind = indexTracker[attributesHead[i]];
			//cout << "Index of head value in body scheme is " << ind << endl;
			tempVal = attributesBody[ind];
			//cout << "Tempval is " << tempVal << endl;
			attributesBody[ind] = attributesBody[i];
			//cout << "AttributesBody[ind] is " << attributesBody[ind] << endl;
			attributesBody[i] = tempVal;
			//cout << "attributesBody[i] is " << attributesBody[i] << endl;
			//cout << "positions swapped were " << i << "and " << ind << endl;
			//cout << endl << "Scheme after swap is ";

			for(unsigned int j = 0; j < attributesBody.size(); ++j) {
				//cout << attributesBody[j] << " ";
			}
			//cout << endl;
			for(unsigned int j = 0; j < tuples.size();++j) {
				//ncout  << "swapping tuple positions accordingly" << endl;
				tempVal = tuples[j][ind];
				//cout << "Tempval is " << tempVal << endl;
				tuples[j][ind] = tuples[j][i];
				//cout << "tuples[j][ind] is " << tuples[j][ind] << endl;
				tuples[j][i] = tempVal;
				//cout << "tuples[j][i] is " << tuples[j][i] << endl;
				//cout << endl << "Tuple after being swapped is now";
				for(unsigned int k = 0; k < tuples[j].size(); ++k) {
					 //cout << " " << tuples[j][k];
				}
				//cout << endl;
			}
			indexTracker[attributesBody[ind]] = ind;
			indexTracker[attributesBody[i]] = i;
		}
	}
	relationScheme.setAttributes(attributesBody);
	//cout << "relation scheme after swapping is now ";
	for(unsigned int i = 0; i < attributesBody.size(); ++i) {
	//cout << attributesBody[i] << " ";
	}
	//cout << endl;	
	for(unsigned int i = 0; i < tuples.size();++i) {
		//cout << "Tuple after being rearranged is ";
		for(unsigned int j = 0; j < tuples[i].size(); ++j) {
			//cout << attributesBody[j] << " => " << tuples[i][j];

		}
		//cout << endl;
		relationTuples.insert(tuples[i]);
	}
	//cout << "Relation after rule project is" << name << " " << toString() << endl;
	//cout << "Leaving rule project" << endl;
}

void Relation::unite(Relation resultRelation) {
	/////cout << "Entered unite" << endl;
	vector<string> attributes = resultRelation.relationScheme.getAttributes();
	attributes = relationScheme.getAttributes();
	//This may cause problems, but seems to be what the test cases are after
	string str;
	/////cout << "In coming relation is" << resultRelation.toString() << endl;
	///cout << "Relation we're joining into is" << name << endl;

	for(auto t: resultRelation.relationTuples) {
		if(relationTuples.find(t) == relationTuples.end()) {
			/////cout << "tuple was not found" << endl;
			str = "";
			for(unsigned int i= 0; i < t.size(); ++i) {
//				///cout << "entered into inner loop" << endl;
//				///cout << "i is " << i << endl;
				if(i == 0) {
					str += "  ";
				}
				if(i != t.size()-1) {
//					///cout << "Entered into if statement" << endl;
					str += attributes[i] + "=" + t[i] + ", ";
				}
				else  {
//					///cout << "entered into else statement" << endl;
					str += attributes[i] + "=" + t[i];
				}
			}
			str += "\n";
			cout << str;
			relationTuples.insert(t);
		}
	}
	///cout << "Left for loop" << endl;


	return;
}

