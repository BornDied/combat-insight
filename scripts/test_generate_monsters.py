import io
import json
import unittest
from unittest.mock import patch

import generate_monsters as generator


class GeneratorSafetyTest(unittest.TestCase):
    def test_api_error_does_not_become_empty_snapshot(self):
        with patch.object(generator.urllib.request, "urlopen", return_value=io.BytesIO(
            json.dumps({"error": {"code": "badquery"}}).encode()
        )):
            with self.assertRaises(ValueError):
                generator.fetch_rows()

    def test_missing_bucket_is_rejected(self):
        with patch.object(generator.urllib.request, "urlopen", return_value=io.BytesIO(b"{}")):
            with self.assertRaises(ValueError):
                generator.fetch_rows()

    def test_empty_first_page_is_rejected(self):
        with patch.object(generator.urllib.request, "urlopen", return_value=io.BytesIO(b'{"bucket": []}')):
            with self.assertRaises(ValueError):
                generator.fetch_rows()

    def test_partial_cache_cannot_silently_zero_combat_stats(self):
        with self.assertRaises(ValueError):
            generator.generate([{"id": ["1"], "name": "Man", "hitpoints": 10}])

    def test_challenge_mode_uses_normal_base_not_scaled_infobox(self):
        rows = [
            {"id": ["7550", "7553"], "page_name": "Great Olm", "page_name_sub": "Great Olm#Left claw",
             "hitpoints": 600, "defence_level": 175, "magic_level": 87, "attribute": ["xerician"]},
            {"id": ["7553"], "page_name": "Great Olm", "page_name_sub": "Great Olm#Challenge Mode",
             "hitpoints": 600, "defence_level": 262, "magic_level": 131, "attribute": ["xerician"]},
        ]
        records, ambiguous = generator.generate(rows)
        by_id = {record[0]: record for record in records}
        self.assertEqual([], ambiguous)
        self.assertEqual(175, by_id[7553][1])
        self.assertIn("cox_cm", by_id[7553][-2])
        self.assertNotIn("cox_cm", by_id[7550][-2])

    def test_maiden_phase_retains_full_health_bar_maximum(self):
        rows = [{"id": ["10814"], "page_name": "The Maiden of Sugadinti",
                 "page_name_sub": "The Maiden of Sugadinti#Entry 30%", "hitpoints": 600,
                 "defence_level": 100, "magic_level": 200}]
        records, ambiguous = generator.generate(rows)
        self.assertEqual({10814, 10815, 10816, 10817}, {record[0] for record in records})
        self.assertTrue(all(record[3] == 2000 for record in records))
        self.assertEqual([], ambiguous)


if __name__ == "__main__":
    unittest.main()
