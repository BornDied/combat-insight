import unittest
from compare_monsters import compare_snapshots
class ComparisonTest(unittest.TestCase):
 def test_identifies_changed_and_removed_ids(self):
  self.assertEqual(compare_snapshots({1:('a','0'),2:('b','0')},{2:('c','1'),3:('d','0')}),{'added':[3],'removed':[1],'changed':[2],'ambiguous':[2]})
 def test_unchanged(self):
  self.assertEqual(compare_snapshots({1:('a','0')},{1:('a','0')})['changed'],[])
