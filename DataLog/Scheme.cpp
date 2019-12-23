#include "Scheme.h"
Scheme::Scheme() {
	return;
}

void Scheme::setName(string Name) {
	name = Name;
	return;
}

void Scheme::addAttribute(string attribute) {
	attributes.push_back(attribute);
	return;
}

void Scheme::setAttributes(vector<string> newAttributes) {
	attributes = newAttributes;
}

vector<string> Scheme::getAttributes() {
	return attributes;
}

void Scheme::changeAttribute(string newName, int index) {
	attributes[index] = newName;
	return;
}

void Scheme::removeAttribute(int index) {
	vector<int> dummyVec;
	//cout << "test" << endl;
	//cout << dummyVec.size();
	//cout << "Attributes before are" << endl;
	for(unsigned int i = 0; i < attributes.size(); ++i) {
		//cout << attributes[i] << " ";
	}
	//cout << endl;
	attributes.erase(attributes.begin()+index);
	//cout << "Attributes now are" << endl;
	if(attributes.size() != 0) {
		for(unsigned int i = 0; i < attributes.size(); ++i ) {
			//cout << attributes[i] << " ";
		}
		//cout << endl;
	}
	else {
		//cout << "Size is zero" << endl;
	}
	return;
}
