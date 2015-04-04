__author__ = 'ctiwary'

import pandas as pd
from sklearn import preprocessing
import numpy as np

class ContinuousFeatureStandardization:
    def __init__(self):
        pass

    #Rescaling
    df = pd.read_csv("../../../../data/chapter3/adult.data.csv")
    min_max_scaler = preprocessing.MinMaxScaler()
    X_train_minmax = min_max_scaler.fit_transform(df['age'].astype(float))
    print min(X_train_minmax),max(X_train_minmax)

    #Mean Standardization
    df = pd.read_csv("../../../../data/chapter3/abalone.data.csv")
    test= (preprocessing.scale(df['Height']))
    print test.mean()
    print test.std()

    #unit norm
    weights =  sorted(np.arange(float(14), 0.05, -1.0))
    weight_norm = np.linalg.norm(weights)
    weights = weights/weight_norm
    print np.linalg.norm(weights)