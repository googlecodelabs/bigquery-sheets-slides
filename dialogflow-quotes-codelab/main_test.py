"""
Tests for main.py.
"""

import json
import main
import unittest

class MainTestCase(unittest.TestCase):

  def setUp(self):
    main.app.testing = True
    self.app = main.app.test_client()

  def _do_request(self, data):
    return self.app.post('/quotesearch', content_type='application/json',
                         data=json.dumps(data))

  def _do_request_with_inputs(self, action, parameters):
    return self._do_request({
        'result': {
            'parameters': parameters,
            'action': action
        }
    })

  # Performs a request to get a pre-defined response with the given action and
  # parameters, and returns the response string, checking that the response
  # displayText and speech response are the same.
  def _get_response_text(self, action, parameters):
    response = self._do_request_with_inputs(action, parameters)
    message = json.loads(response.data)
    response_text = message['displayText']
    response_speech = message['speech']
    self.assertEqual(response_speech, response_text)
    return response_text

  # Performs a request to get an event with the given action and parameters,
  # and returns the name and data for the event.
  def _get_response_event(self, action, parameters):
    response = self._do_request_with_inputs(action, parameters)
    message = json.loads(response.data)
    name = message['followupEvent']['name']
    data = message['followupEvent']['data']
    return name, data


  """ Tests for bad requests """

  def _test_bad_request(self, response, expected_message):
    self.assertEqual('400 BAD REQUEST', response.status)
    data = json.loads(response.data)
    self.assertEqual(expected_message, data['message'])

  def test_no_json_body(self):
    response = self.app.post('/quotesearch')
    self._test_bad_request(response,
                           'No json body was provided in the request.')

  def test_result_not_in_request(self):
    response = self._do_request({'irrelevant': 'doesnt matter'})
    self._test_bad_request(response,
                           '"result" was not provided in the request body.')

  def test_no_action_provided(self):
    response = self._do_request({'result': {}})
    self._test_bad_request(response,
                           'No "action" was provided in the request.')

  def test_unrecognized_action(self):
    response = self._do_request({'result': {'action': 'make_some_tea'}})
    self._test_bad_request(response,
                           'Request action unrecognized: "make_some_tea"')


  """ Tests for action get_quote_response """

  def test_get_quote_response(self):
    response_text = self._get_response_text('get_quote_response', {})
    self.assertIn('Here is a quote by', response_text)

  def test_get_quote_response_by_author(self):
    response_text = self._get_response_text(
        'get_quote_response',
        {'author': 'Grace Hopper'})
    self.assertIn('Here is a quote by Grace Hopper', response_text)

  def test_get_quote_response_by_topic(self):
    response_text = self._get_response_text(
        'get_quote_response',
        {'topic': 'mathematics'})
    self.assertIn('Here is a quote by', response_text)
    self.assertIn('math', response_text.lower())

  def test_get_quote_response_by_author_and_topic(self):
    response_text = self._get_response_text(
        'get_quote_response',
        {'author': 'Grace Hopper',
         'topic': 'mathematics'})
    self.assertIn('Here is a quote by Grace Hopper', response_text)
    self.assertIn('math', response_text.lower())

  def test_get_quote_response_parameter_capitalization(self):
    response_text = self._get_response_text(
        'get_quote_response',
        {'auThOr': 'grace hOpPer',
         'tOPIc': 'mAthEmatIcs'})
    self.assertIn('Here is a quote by Grace Hopper', response_text)
    self.assertIn('math', response_text.lower())

  def test_get_quote_response_no_match(self):
    response_text = self._get_response_text(
        'get_quote_response',
        {'author': 'ksdjdkj'})
    self.assertEqual('I have no matching quote.', response_text)

  def test_get_quote_response_unrecognized_parameter(self):
    response = self._do_request_with_inputs('get_quote_response',
                                            {'diameter': 4})
    self._test_bad_request(response,
                           'Unrecognized parameter in request: diameter')


  """ Tests for action get_quote_event """

  def test_get_quote_event(self):
    name, data = self._get_response_event('get_quote_event', {})
    self.assertEqual('respond_with_quote', name)
    self.assertTrue(data['quote'])
    self.assertTrue(data['author'])

  def test_get_quote_event_by_author(self):
    name, data = self._get_response_event(
        'get_quote_event',
        {'author': 'Grace Hopper'})
    self.assertEqual('respond_with_quote', name)
    self.assertTrue(data['quote'])
    self.assertEqual('Grace Hopper', data['author'])

  def test_get_quote_event_by_topic(self):
    name, data = self._get_response_event(
        'get_quote_event',
        {'topic': 'mathematics'})
    self.assertEqual('respond_with_quote', name)
    self.assertIn('math', data['quote'].lower())
    self.assertTrue(data['author'])

  def test_get_quote_event_by_author_and_topic(self):
    name, data = self._get_response_event(
        'get_quote_event',
        {'author': 'Grace Hopper',
         'topic': 'mathematics'})
    self.assertEqual('respond_with_quote', name)
    self.assertIn('math', data['quote'].lower())
    self.assertEqual('Grace Hopper', data['author'])

  def test_get_quote_event_parameter_capitalization(self):
    name, data = self._get_response_event(
        'get_quote_event',
        {'auThOr': 'grace hOpPer',
         'tOPIc': 'mAthEmatIcs'})
    self.assertEqual('respond_with_quote', name)
    self.assertIn('math', data['quote'].lower())
    self.assertEqual('Grace Hopper', data['author'])

  def test_get_quote_event_no_match(self):
    name, data = self._get_response_event(
        'get_quote_event',
        {'author': 'asdfjkjhd'})
    self.assertEqual('respond_with_quote', name)
    self.assertNotIn('quote', data)
    self.assertNotIn('author', data)

  def test_get_quote_event_unrecognized_parameter(self):
    response = self._do_request_with_inputs('get_quote_event', {'diameter': 4})
    self._test_bad_request(response,
                           'Unrecognized parameter in request: diameter')

  """ Tests for action get_bio_response """

  def test_get_bio_response(self):
    response_text = self._get_response_text(
        'get_bio_response',
        {'author': 'Grace Hopper'})
    self.assertIn('computer scientist', response_text.lower())

  def test_get_bio_response_parameter_capitalization(self):
    response_text = self._get_response_text(
        'get_bio_response',
        {'auThOr': 'grace hOpPer'})
    self.assertIn('computer scientist', response_text.lower())

  def test_get_bio_response_no_match(self):
    response_text = self._get_response_text(
        'get_bio_response',
        {'author': 'asdkfjasdf'})
    self.assertEqual('I have no matching bio.', response_text)

  def test_get_bio_response_unrecognized_parameter(self):
    response = self._do_request_with_inputs('get_bio_response',
                                            {'author': 'Grace Hopper',
                                             'diameter': 4})
    self._test_bad_request(response,
                           'Unrecognized parameter in request: diameter')

  def test_get_bio_response_missing_author_parameter(self):
    response = self._do_request_with_inputs('get_bio_response', {})
    self._test_bad_request(response,
                           'No author parameter provided in request for bio.')


  """ Tests for action get_bio_event """

  def test_get_bio_event(self):
    name, data = self._get_response_event(
        'get_bio_event',
        {'author': 'Grace Hopper'})
    self.assertEqual('respond_with_bio', name)
    self.assertIn('computer scientist', data['bio'].lower())

  def test_get_bio_event_parameter_capitalization(self):
    name, data = self._get_response_event(
        'get_bio_event',
        {'auThOr': 'grace hOpPer'})
    self.assertEqual('respond_with_bio', name)
    self.assertIn('computer scientist', data['bio'].lower())

  def test_get_bio_event_no_match(self):
    name, data = self._get_response_event(
        'get_bio_event',
        {'author': 'asdkjfaksjdhf'})
    self.assertEqual('respond_with_bio', name)
    self.assertNotIn('bio', data)

  def test_get_bio_event_unrecognized_parameter(self):
    response = self._do_request_with_inputs('get_bio_event',
                                            {'author': 'Grace Hopper',
                                             'diameter': 4})
    self._test_bad_request(response,
                           'Unrecognized parameter in request: diameter')

  def test_get_bio_event_missing_author_parameter(self):
    response = self._do_request_with_inputs('get_bio_event', {})
    self._test_bad_request(response,
                           'No author parameter provided in request for bio.')


if __name__ == '__main__':
  unittest.main()
