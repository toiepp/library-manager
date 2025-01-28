import { IUser } from 'app/shared/model/user.model';
import { ILibrary } from 'app/shared/model/library.model';

export interface IBook {
  id?: number;
  isbn?: string;
  title?: string;
  author?: string | null;
  publishingYear?: number | null;
  user?: IUser | null;
  library?: ILibrary | null;
}

export const defaultValue: Readonly<IBook> = {};
