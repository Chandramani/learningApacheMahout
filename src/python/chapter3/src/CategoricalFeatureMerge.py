__author__ = 'ctiwary'

import pandas as pd

class CategoricalFeatureMerge:
    def __init__(self):
        pass

    df = pd.read_csv("../../../../data/chapter3/adult.data.csv")
    print "education against income"
    print pd.crosstab(df['IncomeGreaterThan50K'],df['education'])
    print pd.crosstab(df['IncomeGreaterThan50K'],df['education']).apply(lambda r: r/r.sum(), axis=0)


    # 10th   11th   12th   1st-4th   5th-6th   7th-8th   9th Preschool (Very Low as > 90% earn less)
    # Assoc-acdm   Assoc-voc Some-college HS-grad (Low as > 70% earn less)
    # Bachelors Masters (Medium as ~ 50 earn less)
    # Prof-school Doctorate (High as > 70% earn more)
    list_very_low_income_edu = ["10th","11th","12th","1st-4th","5th-6th","7th-8th","9th","Preschool"]
    list_low_income_edu = ["Assoc-acdm", "Assoc-voc", "Some-college", "HS-grad"]
    list_medium_income_edu = ["Bachelors", "Masters"]
    list_high_income_edu = ["Prof-school", "Doctorate"]
    df['education'].loc[df['education'].isin(list_very_low_income_edu)] = 'VeryLow'
    df['education'].loc[df['education'].isin(list_low_income_edu)] = 'Low'
    df['education'].loc[df['education'].isin(list_medium_income_edu)] = 'Medium'
    df['education'].loc[df['education'].isin(list_high_income_edu)] = 'High'
    print "unique vlaues in education after merging categories" 
    print df['education'].unique()
    df.to_csv("../../../../data/chapter3/adult.data.merged.csv", index=False)
