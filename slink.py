def algsort(l):
    m  = [x for x in enumerate(l)]
    m.sort(key=lambda x: x[1])
    return [x[0] for x in m]

def from_p_repr(L, P, n):
    sorted_idx = algsort(L)
    node_ids = list(xrange(n))
    Z = [[0]*4 for i in range(n-1)]

    for i in range(n-1):
        cl = sorted_idx[i]
        pi = P[cl]
        if node_ids[cl] <  node_ids[pi]:
            Z[i][0] = node_ids[cl]
            Z[i][1] = node_ids[pi]
        else:
            Z[i][0] = node_ids[pi]
            Z[i][1] = node_ids[cl]
        Z[i][2] = L[cl]
        node_ids[pi] = n+i
        Z[i][3] = 0

    calc_cluster_sizes(Z,n)
    print "Z: \n%s" % str(Z)
    print
    print clusters(Z,n,4)



def clusters(Z, n, k):
    lut = [0]*n
    clusters = [[i] for i in range(n)]
    clusters_ids =  list(range(n))
    
    i = 0
    while n-i > k:
        #print zip(clusters_ids, clusters)
        cl, cr = Z[i][:2]
        ind_l, ind_r = cl%n, cr%n
        print ind_l, ind_r
        union = clusters[ind_l]+clusters[ind_r]
        for p in union:
            clusters[p] = union
            clusters_ids[p] = clusters_ids[ind_r]
        i += 1
    #print zip(clusters_ids, clusters)

    print
    processed = []
    for c_id,c in zip(clusters_ids,clusters):
        if c_id not in processed:
            processed.append(c_id)
            print c_id, c
            for p in c:
                lut[p] = c_id

    return lut


def calc_cluster_sizes(Z,n):
    for i in range(n-1):
        cl = Z[i][0]
        cr = Z[i][1]
        Z[i][3] += Z[cl-n][3] if cl >= n else 1
        Z[i][3] += Z[cr-n][3] if cr >= n else 1

def slink(distance, n):
    M, L, P = [0.]*n, [0.]*n, list(xrange(n))
    
    for i in range(n):
        P[i] = i
        L[i] = 2**64

        for j in range(i):
            M[j] = distance[i+n*j]

        for j in range(i):
            if L[j] >= M[j]:
                M[P[j]] = min(M[P[j]], L[j])
                L[j] = M[j]
                P[j] = i
            else:
                M[P[j]] = min(M[P[j]], M[j])

        for j in range(i):
            if L[j] >= L[P[j]]:
                P[j] = i

        print "step %d"%i
        print "M: %s" % str(M)
        print "P: %s" % str(P)
        print "L: %s" % str(L)
        print

    from_p_repr(L,P,n)

def get_distance(points):
    dm = [[0]*len(points) for i in range(len(points))]
    for i in range(len(points)):
        for j in range(len(points)):
            dm[i][j] = abs(points[i]-points[j])
    d = []
    for l in dm:
        d += l
    return d


points = (1,2,5,6,9,10,12,13,15,16,2,1)
d = get_distance(points)
print str(points), len(points)
slink(d, len(points))



