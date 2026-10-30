import copy
import unittest

from audit_wiki_variants import audit


def monster(npc_id, version='', defence=100):
    return {'id': npc_id, 'name': 'Test target', 'version': version,
            'skills': {'def': defence, 'magic': 50, 'hp': 200},
            'offensive': {'magic': 0},
            'defensive': dict(flat_armour=0, stab=1, slash=2, crush=3,
                              magic=4, light=5, standard=6, heavy=7)}


class WikiVariantAuditTest(unittest.TestCase):
    def setUp(self):
        self.local = {123: ('100', '50', '200', '0', '1', '0', '1', '2',
                            '3', '4', '5', '6', '7', '', '0', '0', '', '0')}

    def test_duplicate_id_variants_are_compared_individually(self):
        result = audit([monster(123, 'Normal'), monster(123, 'Phase 2', 120)], self.local)
        self.assertEqual({123: ['Test target (Normal)', 'Test target (Phase 2)']},
                         result['duplicate_ids'])
        self.assertEqual([{'id': 123, 'name': 'Test target', 'version': 'Phase 2',
                           'differences': {'skills.def': {'local': 100, 'wiki': 120}}}],
                         result['numeric_differences'])

    def test_unmatched_variants_are_not_counted_as_species(self):
        result = audit([monster(456, 'Normal'), monster(456, 'Hard')], self.local)
        self.assertEqual(2, len(result['unmatched']))
        self.assertEqual(1, result['unique_wiki_ids'])
        self.assertEqual(2, result['wiki_variant_records'])

    def test_virtual_calculator_ids_are_separate_from_missing_live_npcs(self):
        result = audit([monster(100001, 'Glyph')], self.local)
        self.assertEqual([], result['unmatched'])
        self.assertEqual(100001, result['non_live_records'][0]['id'])

    def test_audit_does_not_mutate_inputs_or_hide_ambiguous_local_records(self):
        self.local[123] = self.local[123][:-1] + ('1',)
        wiki = [monster(123)]
        before = copy.deepcopy((wiki, self.local))
        result = audit(wiki, self.local)
        self.assertEqual([{'id': 123, 'name': 'Test target', 'version': ''}],
                         result['ambiguous_matches'])
        self.assertEqual([], result['numeric_differences'])
        self.assertEqual(before, (wiki, self.local))


if __name__ == '__main__':
    unittest.main()
