export interface ILibrary {
  id?: number;
  name?: string;
  postalAddress?: string;
}

export const defaultValue: Readonly<ILibrary> = {};
