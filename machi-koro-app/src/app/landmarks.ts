export const LANDMARKS: Landmark[] =
  [ {
      id: "train",
      name: "Train Station",
      cost: 4
    },
    {
      id: "shopping",
      name: "Shopping Mall",
      cost: 10
    },
    {
      id: "amusement",
      name: "Amusement Park",
      cost: 16
    },
    {
      id: "radio",
      name: "Radio Tower",
      cost: 22
    }

  ]

export class Landmark {
  id: string;
  name: string;
  cost: number;
}
