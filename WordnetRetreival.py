import networkx as nx
from nltk.corpus import wordnet as wn
import nltk # language toolkit, needed for wordnet

# download wordnet
nltk.download('wordnet')

# Initialize an empty graph
G = nx.Graph()

# find similarity
def safe_wup(syn1, syn2):
    sim = syn1.wup_similarity(syn2)
    return sim if sim is not None else 0.0

# Iterate over all adjective synsets
for synset in wn.all_synsets(pos=wn.ADJ):
    lemmas = synset.lemmas()

    # Positive edges: synonyms in same synset
    for lemma in lemmas:
        for other in lemmas:
            if lemma.name() != other.name():
                sim = safe_wup(lemma.synset(), other.synset())
                G.add_edge(lemma.name(), other.name(), weight=sim)  # positive weight

        # Similar-to edges
        for similar_synset in lemma.synset().similar_tos():
            sim = safe_wup(lemma.synset(), similar_synset)
            for other in similar_synset.lemmas():
                if lemma.name() != other.name():
                    G.add_edge(lemma.name(), other.name(), weight=sim)  # positive weight

    # Negative edges: antonyms
    for lemma in lemmas:
        for ant in lemma.antonyms():
            sim = safe_wup(lemma.synset(), ant.synset())
            G.add_edge(lemma.name(), ant.name(), weight=-sim)  # negative weight

# Create mapping from node names to integer indices
node_to_idx = {node: i for i, node in enumerate(G.nodes())}

# Write to file
with open("wordnet_edges.txt", "w", encoding="utf-8") as f:
    for u, v, data in G.edges(data=True):
        idx_u = node_to_idx[u]
        idx_v = node_to_idx[v]
        weight = data['weight']
        f.write(f"{idx_u} {idx_v} {weight:.4f}\n")

print(f"Graph has {G.number_of_nodes()} nodes and {G.number_of_edges()} edges.")
print("Edge list saved to wordnet_edges.txt")
