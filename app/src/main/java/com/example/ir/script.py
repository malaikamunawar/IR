import nltk
nltk.download('punkt')
nltk.download('stopwords')
nltk.download('averaged_perceptron_tagger')

from nltk.corpus import stopwords
from nltk.tokenize import word_tokenize
from nltk.stem import SnowballStemmer

def preprocess_query(query):
    # Tokenize the query
    tokens = word_tokenize(query)

    # Remove stopwords
    stop_words = set(stopwords.words('english'))
    filtered_tokens = [word for word in tokens if word.lower() not in stop_words]

    # Perform stemming
    stemmer = SnowballStemmer('english')
    stemmed_tokens = [stemmer.stem(word) for word in filtered_tokens]

    # Remove verbs (you may need to customize this part based on your specific requirements)
    pos_tags = nltk.pos_tag(stemmed_tokens)
    re_filtered_tokens = [word for word, pos in pos_tags if pos not in ['VB', 'VBD', 'VBG', 'VBN', 'VBP', 'VBZ']]

    # Join the tokens back into a string
    preprocessed_query = ' '.join(filtered_tokens)

    return preprocessed_query
