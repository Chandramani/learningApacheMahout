__author__ = 'ctiwary'


from sklearn.decomposition import PCA
from sklearn import datasets
from sklearn import metrics
from sklearn.tree import DecisionTreeClassifier
from sklearn import tree
#import pydot, StringIO
#from IPython.core.display import Image


df = datasets.load_iris()
model = DecisionTreeClassifier(criterion='entropy')
print "Building the model using all the data"
model.fit(df.data, df.target)
expected = df.target
predicted = model.predict(df.data)
# dot_data = StringIO.StringIO()
# tree.export_graphviz(model, out_file=dot_data,feature_names=['sepal_len', 'sepal_wid', 'petal_len', 'petal_wid', 'class'])
# graph = pydot.graph_from_dot_data(dot_data.getvalue())
# graph.write_png('rawData.png')
# Image(filename='rawData.png')
print "performance of model using all the data"
print(metrics.classification_report(expected, predicted))
print(metrics.confusion_matrix(expected, predicted))

principal_components = PCA()
pca_data = principal_components.fit_transform(df.data)
print "The proportion of variance explained by each component"
print principal_components.explained_variance_ratio_
print "Building the model using the first principal component"
model.fit(pca_data[:,[0]],df.target)
expected = df.target
predicted = model.predict(pca_data[:,[0]])
# dot_data = StringIO.StringIO()
# tree.export_graphviz(model, out_file=dot_data)#, feature_names=['pc1', 'pc2'])
# graph = pydot.graph_from_dot_data(dot_data.getvalue())
# graph.write_png('pcaData.png')
# Image(filename='pcaData.png')
print "performance of model using the first principal component"
print(metrics.classification_report(expected, predicted))
print(metrics.confusion_matrix(expected, predicted))