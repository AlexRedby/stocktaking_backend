# HTTP API contract

The web service exposes its supported endpoints under `/api` and returns JSON.

## Search items

`GET /api/craftable-items` accepts an optional `filter` query parameter and
returns only items that have at least one recipe. Items without recipes are not
included. A missing or blank filter returns the complete current craftable-item
catalog. Search is case-insensitive. Whitespace and `#`, `/`, `"`, `,`, and `.`
separate terms. Terms are matched in order at word boundaries within either
`fullName` or `shortName`.

A successful response is `200 OK` with an array:

```json
[
  {
    "id": "5c0a840b86f7742ffa4f2482",
    "fullName": "THICC Items case",
    "shortName": "T H I C C"
  }
]
```

## Build a crafting tree

`GET /api/crafting-tree` requires `target_item_id`. The value must
be one 24-character hexadecimal item ID. Letter case is accepted and normalized
before lookup.

A successful response is `200 OK` with a React Flow graph containing `nodes`
and `edges`. Each result node groups its alternative recipes in `data.recipes`.
Recipe IDs are also React Flow source-handle IDs. An edge belongs to the recipe
named by `sourceHandle`; its ID is unique for that recipe and required item.
Output and input quantities are represented by `outputCount` and
`requiredItemCount`:

```json
{
  "nodes": [
    {
      "id": "result-item-id",
      "data": {
        "label": "Result",
        "fullName": "Result item",
        "shortName": "Result",
        "image": "https://example.com/result.png",
        "recipes": [
          {
            "id": "recipe-id",
            "outputCount": 2.0,
            "station": {
              "id": "station-id",
              "name": "Workbench",
              "level": 2,
              "image": "https://example.com/workbench.png"
            }
          }
        ]
      }
    }
  ],
  "edges": [
    {
      "id": "recipe-id:required-item-id",
      "source": "result-item-id",
      "sourceHandle": "recipe-id",
      "target": "required-item-id",
      "requiredItemCount": 3.0
    }
  ]
}
```

A valid ID that does not exist in the current graph returns `404 Not Found`.

## Errors

Errors from these endpoints use stable `code` and `message` fields. Errors tied
to a query parameter also include `parameter`:

```json
{
  "code": "invalid_parameter",
  "message": "Query parameter 'target_item_id' must be a 24-character hexadecimal ID",
  "parameter": "target_item_id"
}
```

| Status | Code | Meaning |
| --- | --- | --- |
| `400` | `missing_parameter` | `target_item_id` was not provided. |
| `400` | `invalid_parameter` | `target_item_id` is not a 24-character hexadecimal ID. |
| `404` | `item_not_found` | No current graph item has the requested ID. |
| `502` | `upstream_error` | Tarkov.dev returned GraphQL errors or an invalid response. |
| `503` | `upstream_unavailable` | Tarkov.dev transport attempts failed or timed out. |
| `500` | `internal_error` | The request failed unexpectedly without exposing internal details. |
