import sqlite3
import math

# Connect to SQLite database
conn = sqlite3.connect('map.db')
cursor = conn.cursor()

# Create a view to get the coordinate differences and node types
cursor.execute('''
CREATE VIEW IF NOT EXISTS node_distances AS
SELECT
    nc.node1,
    nc.node2,
    (n1.x - n2.x) AS dx,
    (n1.y - n2.y) AS dy,
    n1.node_type AS node1_type,
    n2.node_type AS node2_type
FROM
    node_connection nc
JOIN
    node n1 ON nc.node1 = n1.node_id
JOIN
    node n2 ON nc.node2 = n2.node_id;
''')

# Fetch the coordinate differences and node types
cursor.execute('SELECT node1, node2, dx, dy, node1_type, node2_type FROM node_distances')
distances = cursor.fetchall()

# Calculate the Euclidean distance and update the weights
for node1, node2, dx, dy, node1_type, node2_type in distances:
    # If both nodes are either stair or lift, set weight to 400
    if (node1_type in ['stair', 'lift']) and (node2_type in ['stair', 'lift']):
        weight = 400
    # If one node is right and the other is left, set weight to 1000
    elif (node1_type == 'right' and node2_type == 'left') or (node1_type == 'left' and node2_type == 'right'):
        weight = 1000
    # Otherwise, calculate the Euclidean distance
    else:
        weight = math.sqrt(dx * dx + dy * dy) # Formula for finding distances using coordinates
        weight = round(weight)  # Round the weight to the nearest integer
    
    cursor.execute('UPDATE node_connection SET weight = ? WHERE node1 = ? AND node2 = ?', (weight, node1, node2))

# Commit the changes and close the connection
conn.commit()
conn.close()
