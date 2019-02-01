# nearest_neighbor.py
"""Volume 2: Nearest Neighbor Search.
<Name>
<Class>
<Date>
"""

import numpy as np
from scipy import linalg as la
from scipy import spatial
from scipy import stats
# Problem 1
def exhaustive_search(X, z):
    """Solve the nearest neighbor search problem with an exhaustive search.

    Parameters:
        X ((m,k) ndarray): a training set of m k-dimensional points.
        z ((k, ) ndarray): a k-dimensional target point.

    Returns:
        ((k,) ndarray) the element (row) of X that is nearest to z.
        (float) The Euclidean distance from the nearest neighbor to z.
    """
    #this gives an array of the distance of each row of X to z
    distanceArray = la.norm(X - z, axis = 1)
    return X[distanceArray.argmin(), :z.shape[0]], min(distanceArray) 
   
    #argmin returns the index of the row closest to z, :z.shape[0] makes sure we return the whole row, min(distanceArray) returns the euclidean distance to z's nearest neighbor
    
    raise NotImplementedError("Problem 1 Incomplete")


# Problem 2: Write a KDTNode class.
class KDTNode():
    """A node that contains the value x, as well as references to the nodes immediately below it
    
    Attributes: 
        value (numpy.ndarray): contains an array of k x 1 dimensions, is stored as the value of the node
        left (KDTNode): points to the node below and to the left of the current node
        right (KDTNode): points to the node below and right of the current node
        pivot( ): contains the value of the pivot level (pivot levels are in mod k)
    """

    def __init__(self, x):
        """Sets the value of the node to X, and initializes left, right, and pivot to be None pointers
        
        Parameters:
            x (numpy.ndarray): A numpy array of shape k x 1 containing the points of the node
        """
        if type(x) != np.ndarray:
            raise TypeError ("x must be a numpy.ndarray")
        self.value = x
        self.left = None
        self.right = None
        self.pivot = None
        return
# Problems 3 and 4
class KDT:
    """A k-dimensional binary tree for solving the nearest neighbor problem.

    Attributes:
        root (KDTNode): the root node of the tree. Like all other nodes in
            the tree, the root has a NumPy array of shape (k,) as its value.
        k (int): the dimension of the data in the tree.
    """
    def __init__(self):
        """Initialize the root and k attributes."""
        self.root = None
        self.k = None

    def find(self, data):
        """Return the node containing the data. If there is no such node in
        the tree, or if the tree is empty, raise a ValueError.
        """
        def _step(current):
            """Recursively step through the tree until finding the node
            containing the data. If there is no such node, raise a ValueError.
            """
            if current is None:                     # Base case 1: dead end.
                raise ValueError(str(data) + " is not in the tree")
            elif np.allclose(data, current.value):
                return current                      # Base case 2: data found!
            elif data[current.pivot] < current.value[current.pivot]:
                return _step(current.left)          # Recursively search left.
            else:
                return _step(current.right)         # Recursively search right.

        # Start the recursive search at the root of the tree.
        return _step(self.root)

    # Problem 3
    def insert(self, data):
        """Insert a new node containing the specified data.

        Parameters:
            data ((k,) ndarray): a k-dimensional point to insert into the tree.

        Raises:
            ValueError: if data does not have the same dimensions as other
                values in the tree.
        """
        newNode = KDTNode(data)
        #if the KDT is empty
        if self.root == None:
            newNode.pivot = 0
            self.root = newNode
            self.k = len(data)
        #if the KDT is not empty
        elif self.root != None:
            if len(data) != self.k:
                raise ValueError("data is not in R^k")
            searching = True
            totalLevel = 0
            curNode = self.root
            while searching == True:
                if np.allclose(data, curNode.value) == True:
                    raise ValueError("KDT cannot have repeats")
                #makes sure pivots are in mod k
                curPivot = totalLevel % self.k
                #newNode goes to right of parent with no child
                if newNode.value[curPivot] >= curNode.value[curPivot] and curNode.right == None:
                    curNode.right = newNode
                    newNode.pivot = (curPivot+1) % self.k
                    searching = False
                #newNode goes to left of parent with no child
                elif newNode.value[curPivot] < curNode.value[curPivot] and curNode.left == None:
                    curNode.left = newNode
                    newNode.pivot = (curPivot+1) % self.k
                    searching = False
                #goes to right of parent
                elif newNode.value[curPivot] >= curNode.value[curPivot]:
                    curNode = curNode.right
                    totalLevel += 1
                #goes to left of parent
                elif newNode.value[curPivot] < curNode.value[curPivot]:
                    curNode = curNode.left
                    totalLevel += 1
        return             
        raise NotImplementedError("Problem 3 Incomplete")

    # Problem 4
    def query(self, z):
        """Find the value in the tree that is nearest to z.

        Parameters:
            z ((k,) ndarray): a k-dimensional target point.

        Returns:
            ((k,) ndarray) the value in the tree that is nearest to z.
            (float) The Euclidean distance from the nearest neighbor to z.
        """
        def KDSearch(current, nearest, dstar):
            """Searches the KDT to see if any values are closer than the current value, runs recursively starting at the root
                
            Parameters: 
                current(KDTNode): The current node the search is at
                nearest(KDTNode): The current nearest node to the target point
                dstar(float): The distance from the nearest node's value to the target point
            Returns:
                nearest(KDTNode): The closest node to the target value
                dstar(float): The distance from the nearest node's value to the target point
            """
            #Base case: dead end
            if current is None:
                return nearest, dstar
            x = current.value
            i = current.pivot
            #check if current is closer to z than nearest
            if la.norm(x - z) < dstar:
                nearest = current
                dstar = la.norm(x-z)
            #search to the left
            if z[i] < x[i]:
                nearest, dstar = KDSearch(current.left, nearest, dstar)
                #search to the right if needed
                if z[i] + dstar >= x[i]:
                    nearest, dstar = KDSearch(current.right,nearest, dstar)
            #search to the right
            else:
                nearest, dstar = KDSearch(current.right, nearest, dstar)
                #search to the left if needed
                if z[i] - dstar <= x[i]:
                    nearest, dstar = KDSearch(current.left, nearest, dstar)
            return nearest, dstar
        node, dstar = KDSearch(self.root,self.root, la.norm(self.root.value - z))
        return node.value, dstar
        raise NotImplementedError("Problem 4 Incomplete")

    def __str__(self):
        """String representation: a hierarchical list of nodes and their axes.

        Example:                           'KDT(k=2)
                    [5,5]                   [5 5]   pivot = 0
                    /   \                   [3 2]   pivot = 1
                [3,2]   [8,4]               [8 4]   pivot = 1
                    \       \               [2 6]   pivot = 0
                    [2,6]   [7,5]           [7 5]   pivot = 0'
        """
        if self.root is None:
            return "Empty KDT"
        nodes, strs = [self.root], []
        while nodes:
            current = nodes.pop(0)
            strs.append("{}\tpivot = {}".format(current.value, current.pivot))
            for child in [current.left, current.right]:
                if child:
                    nodes.append(child)
        return "KDT(k={})\n".format(self.k) + "\n".join(strs)


# Problem 5: Write a KNeighborsClassifier class.
class KNeighborsClassifier:
    """KNeighborsClassifier is a class used to help find the k closest neighbors to a node z
    Attributes:
        k(int): number of closest neighbors in the k-nearest neighbor search
    """
    def __init__(self, n_neighbors):
        """Initializes the k for the k-nearest neighbor search
    
        Parameters:
            n_neighbors(int): number of neighbors for k
        """
        self.k = n_neighbors
        return
    
    def fit(self, X, y):
        """Sets the training set and training labels for the class

        Parameters:
            X((m,k) ndarray): The training set for the class
            y((1,m) ndarray): The training labels for the class

        """
        self.training = spatial.KDTree(X)
        self.labels = y
        return
    
    def predict(self, z):
        """finds the k most nearest neighbors in the KDTree to z, and returns the most common label of those neighbors
    
        Parameters:
            z((1,k) ndarray): Point for which to base n-closest neighbors on
        """
        distances, indices = self.training.query(z, self.k)
        labelList = []
        #makes a list of the labels of the indices
        labelList.append(self.labels[indices])
        #stats.mode returns two arrays, the most common label and the number of times it appears, thus [0] access the most common label array and [0][0] returns the actual label, rather than an array of the label
        return stats.mode(labelList)[0][0]   

# Problem 6
def prob6(n_neighbors, filename="mnist_subset.npz"):
    """Extract the data from the given file. Load a KNeighborsClassifier with
    the training data and the corresponding labels. Use the classifier to
    predict labels for the test data. Return the classification accuracy, the
    percentage of predictions that match the test labels.

    Parameters:
        n_neighbors (int): the number of neighbors to use for classification.
        filename (str): the name of the data file. Should be an npz file with
            keys 'X_train', 'y_train', 'X_test', and 'y_test'.

    Returns:
        (float): the classification accuracy.
    """
    data = np.load("mnist_subset.npz")
    Classifier = KNeighborsClassifier(n_neighbors)
    X_train = data["X_train"].astype(np.float)
    y_train = data["y_train"]
    X_test = data["X_test"].astype(np.float)
    y_test = data["y_test"]
    Classifier.fit(X_train, y_train)
    num_right = 0
    #in the loop, this runs the predict algorithm for each row of x, then compares if this answer matched the label in y. If it did, the num_right is updated. We divide this by 500 to return the percentage correct.
    for i in range(0,500):
         k = Classifier.predict(X_test[i])
         if k == y_test[i]:
             num_right += 1
    return num_right / 500   
     
    raise NotImplementedError("Problem 6 Incomplete")
