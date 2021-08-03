export const LANDMARKS: Landmark[] =
  [ {
      id: "train",
      name: "Train Station",
      description: "Roll 2 dice.",
      cost: 4
    },
    {
      id: "shopping",
      name: "Shopping Mall",
      description: "Earn +1 coin from your own cup and bread establishments.",
      cost: 10
    },
    {
      id: "amusement",
      name: "Amusement Park",
      description: "If you roll matching dice, take another turn after this one.",
      cost: 16
    },
    {
      id: "radio",
      name: "Radio Tower",
      description: "Once every turn you can choose to re-roll your dice.",
      cost: 22
    }

  ]

export class Landmark {
  id: string;
  name: string;
  description: string;
  cost: number;
}
