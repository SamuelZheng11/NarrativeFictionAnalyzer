from narritive_objects.Entity import Entity


class Relationship:
    subjectEntity: Entity;
    objectEntity: Entity;
    relation = "";
    bidirectional = False;

    def __init__(self, subjectEntity: Entity, objectEntity: Entity, relation, bidirectional):
        self.subjectEntity = subjectEntity
        self.objectEntity = objectEntity
        self.relation = relation
        self.bidirectional = bidirectional
