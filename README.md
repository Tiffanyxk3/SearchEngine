# Search Engine Project (Java)

- This is a fully functional search engine supported by a self built multithreaded web crawler and a self built search engine web interface.
- The multithreaded web crawler is created using a work queue that builds an inverted index from a seed URL.
- The search engine web interface is created search the index using embedded Jetty and servlets.

The project contains the following functionalities:
1. An inverted index is created, processing all text files in a directory and its subdirectories, cleaning and parsing the text into word stems. The mapping from word stems to the documents and position within those documents where those word stems were found is stored in the index.
2. Both partial search and exact search are supported. The code tracks the total number of words found in each text file, parses and stems a query file, generates a sorted list of search results from the inverted index, and supports writing those results to a JSON file.
3. A thread-safe inverted index is created to support multithreading. A work queue is used to build and search an inverted index using multiple threads.
