__author__ = 'ctiwary'

import pandas as pd


class CategoricalFeatureToPercentages:
    def __init__(self):
        pass

    df = pd.read_csv("../../../../data/chapter3/adult.data.merged.csv")
    # df['IncomeGreaterThan50K'] = df['IncomeGreaterThan50K'].astype('category')
    # df['education'] = df['education'].astype('category')
    # # print df[['IncomeGreaterThan50K','education']].describe()
    print pd.crosstab(df['IncomeGreaterThan50K'],df['education']).apply(lambda r: r/r.sum(), axis=0)

    # Replace the categories with percentages
