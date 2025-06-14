import { Pipe, PipeTransform } from '@angular/core';

@Pipe({
  name: 'gradeDisplay',
  standalone: true
})
export class GradeDisplayPipe implements PipeTransform {
  transform(value: string): string {
    if (!value) return '';
    const gradeNumber = value.replace('GRADE_', '');
    return `Grade ${gradeNumber}`;
  }
}
